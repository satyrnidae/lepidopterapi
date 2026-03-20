package dev.satyrn.lepidoptera.config.serializers;

import dev.satyrn.lepidoptera.annotations.YamlComment;
import dev.satyrn.lepidoptera.beans.BeanInspection;

import javax.annotation.Nullable;
import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Builds YAML comment maps from {@link YamlComment} annotations and injects them into serialized YAML strings.
 * Pure Java — no Minecraft, Mixin, or Cloth Config dependencies.
 */
class YamlCommentInjector {
    static final String SECTION_IDENTIFIER = "__SECTION";

    private static final String COMMENT_PREFIX = "# ";
    private static final String ENUM_LIST_BULLET = "- ";
    private static final String YAML_DIRECTIVE_PREFIX = "!!";
    private static final String TYPE_LEADER = "Type: ";
    private static final int INDENT = 4;

    static final int DEFAULT_LINE_LENGTH = 120;
    static final int MIN_LINE_LENGTH = 60;

    private final int lineLength;

    YamlCommentInjector(int lineLength) {
        this.lineLength = lineLength;
    }

    /**
     * Builds a flat comment map keyed by YAML path from a config class's {@link YamlComment} annotations.
     * Section header entries are stored at {@code path.__SECTION}.
     */
    Map<String, String> buildNestedCommentMap(Class<?> clazz) throws Exception {
        Map<String, String> comments = new HashMap<>();
        if (BeanInspection.isBean(clazz)) {
            buildCommentsRecursive(clazz, "", comments);
        }
        return comments;
    }

    /**
     * Injects comments from the comment map into a YAML string. Optionally prepends a file-level header comment
     * from {@code configClass}'s {@link YamlComment} annotation if present.
     */
    String injectComments(String yaml, Map<String, String> commentMap, @Nullable Class<?> configClass) {
        List<String> lines = new ArrayList<>(Arrays.asList(yaml.split("\\r?\\n")));
        if (lines.isEmpty()) {
            return yaml;
        }

        if (configClass != null) {
            final @Nullable YamlComment classComment = configClass.getAnnotation(YamlComment.class);
            if (classComment != null) {
                int startIndex = lines.get(0).startsWith(YAML_DIRECTIVE_PREFIX) ? 1 : 0;
                lines.add(startIndex, "");

                String[] commentLines = wordWrap(classComment.value()).split("\\r?\\n");
                for (int i = commentLines.length - 1; i >= 0; i--) {
                    lines.add(startIndex, COMMENT_PREFIX + commentLines[i]);
                }

                lines.add(startIndex, "");
            }
        }

        Deque<String> pathStack = new ArrayDeque<>();

        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            String trimmed = line.trim();

            if (trimmed.startsWith(YAML_DIRECTIVE_PREFIX)) {
                lines.remove(i--);
                continue;
            }
            if (trimmed.isEmpty() || trimmed.startsWith("- ")) {
                continue;
            }

            int lineIndent = line.indexOf(trimmed.charAt(0));
            int indentLevel = lineIndent / INDENT;

            while (indentLevel < pathStack.size()) {
                pathStack.pop();
            }

            if (trimmed.endsWith(":")) {
                String key = trimmed.substring(0, trimmed.length() - 1).trim();
                String fullPath = getCurrentPath(pathStack, key);
                String sectionPath = fullPath + "." + SECTION_IDENTIFIER;

                if (commentMap.containsKey(sectionPath)) {
                    String[] commentLines = commentMap.get(sectionPath).split("\\r?\\n");
                    for (int j = commentLines.length - 1; j >= 0; j--) {
                        lines.add(i, COMMENT_PREFIX + commentLines[j]);
                    }
                    if (i - 1 >= 0 && !lines.get(i - 1).isBlank()) {
                        lines.add(i, "");
                        i += 1;
                    }
                    i += commentLines.length;
                } else if (commentMap.containsKey(fullPath)) {
                    String[] commentLines = commentMap.get(fullPath).split("\\r?\\n");
                    int indent = line.indexOf(key);

                    for (int j = commentLines.length - 1; j >= 0; j--) {
                        lines.add(i, " ".repeat(indent) + COMMENT_PREFIX + commentLines[j]);
                    }
                    i += commentLines.length;
                }

                if (indentLevel >= pathStack.size()) {
                    pathStack.push(key);
                }
            } else if (trimmed.contains(":")) {
                String property = trimmed.split(":", 2)[0].trim();
                String fullPath = getCurrentPath(pathStack, property);

                if (commentMap.containsKey(fullPath)) {
                    int indent = line.indexOf(property);
                    String[] commentLines = commentMap.get(fullPath).split("\\r?\\n");

                    for (int j = commentLines.length - 1; j >= 0; j--) {
                        lines.add(i, " ".repeat(indent) + COMMENT_PREFIX + commentLines[j]);
                    }
                    i += commentLines.length;
                }
            }
        }
        return String.join("\n", lines);
    }

    private void buildCommentsRecursive(Class<?> clazz, String currentPath, Map<String, String> comments)
            throws Exception {
        BeanInfo beanInfo = Introspector.getBeanInfo(clazz);

        for (PropertyDescriptor prop : beanInfo.getPropertyDescriptors()) {
            if (prop.getName().equals("class")) {
                continue;
            }

            Method getter = prop.getReadMethod();
            if (getter != null) {
                final @Nullable YamlComment comment = getter.getAnnotation(YamlComment.class);
                String fullPath = currentPath.isEmpty() ? prop.getName() : currentPath + "." + prop.getName();
                int indentLevel = fullPath.split("\\.").length - 1;

                if (comment != null) {
                    boolean isSection = comment.sectionHeader();
                    String entryPath = isSection ? fullPath + "." + SECTION_IDENTIFIER : fullPath;

                    if (!comment.value().isBlank()) {
                        if (isSection) {
                            comments.put(entryPath, wordWrap(comment.value()));
                        } else {
                            comments.put(entryPath, wordWrap(comment.value(), 0, indentLevel * INDENT));
                        }
                    }

                    if (!comment.note().isEmpty()) {
                        String existing = comments.getOrDefault(entryPath, "");
                        comments.put(entryPath, (existing.isEmpty() ? "" : existing + "\n") +
                                comment.noteLeader() +
                                wordWrap(comment.note(), comment.noteLeader().length(), indentLevel * INDENT));
                    }

                    if (!isSection && comment.emitType()) {
                        String existing = comments.getOrDefault(entryPath, "");
                        comments.put(entryPath, (existing.isEmpty() ? "" : existing + "\n") +
                                TYPE_LEADER +
                                getter.getReturnType().getName());
                    }

                    if (!comment.defaultValue().isEmpty()) {
                        String existing = comments.getOrDefault(entryPath, "");
                        comments.put(entryPath, (existing.isEmpty() ? "" : existing + "\n") +
                                comment.defaultValueLeader() +
                                wordWrap(comment.defaultValue(), comment.defaultValueLeader().length(),
                                        indentLevel * INDENT));
                    }
                }

                if (prop.getPropertyType().isEnum()) {
                    String enumComments = getEnumComments(prop.getPropertyType(), indentLevel);
                    if (!enumComments.isEmpty()) {
                        String entryPath = (comment != null && comment.sectionHeader())
                                ? fullPath + "." + SECTION_IDENTIFIER
                                : fullPath;
                        String existing = comments.getOrDefault(entryPath, "");
                        comments.put(entryPath, (existing.isEmpty() ? "" : existing + "\n") + enumComments);
                    }
                }

                if (BeanInspection.isBean(prop.getPropertyType()) && (comment == null || comment.emitChildren())) {
                    buildCommentsRecursive(prop.getPropertyType(), fullPath, comments);
                }
            }
        }
    }

    String getEnumComments(Class<?> enumClass, int indentLevel) {
        if (!enumClass.isEnum()) {
            return "";
        }

        StringBuilder sb = new StringBuilder(YamlComment.VALID_VALUES_LEADER);
        boolean hasOptions = false;
        for (Field field : enumClass.getDeclaredFields()) {
            if (field.isEnumConstant()) {
                hasOptions = true;
                final String enumName = field.getName();
                sb.append("\n").append(ENUM_LIST_BULLET).append(enumName);
                final @Nullable YamlComment comment = field.getAnnotation(YamlComment.class);
                if (comment != null) {
                    String[] commentLines = comment.value().split("\\r?\\n");
                    int indentLength = enumName.length() + 2;

                    sb.append(": ")
                            .append(wordWrap(commentLines[0], indentLength + ENUM_LIST_BULLET.length(),
                                    indentLevel * INDENT));

                    for (int i = 1; i < commentLines.length; i++) {
                        sb.append("\n")
                                .append(" ".repeat(ENUM_LIST_BULLET.length()))
                                .append(" ".repeat(indentLength))
                                .append(wordWrap(commentLines[i], indentLength + ENUM_LIST_BULLET.length(),
                                        indentLevel * INDENT));
                    }
                }
            }
        }
        return hasOptions ? sb.toString() : "";
    }

    String wordWrap(String text) {
        return this.wordWrap(text, 0, 0);
    }

    String wordWrap(String text, int baseIndent, int preIndent) {
        if (text.isEmpty()) {
            return "";
        }

        int minLength = Math.max(MIN_LINE_LENGTH, this.lineLength / 2);
        int effectiveLineLength = Math.max(minLength, this.lineLength - baseIndent - preIndent) -
                COMMENT_PREFIX.length();

        StringBuilder result = new StringBuilder();
        String[] paragraphs = text.split("(?<=\n)", -1);

        for (String paragraph : paragraphs) {
            if (paragraph.equals("\n")) {
                result.append("\n");
                continue;
            }

            String trimmed = paragraph.replaceFirst("^\\s+", "");
            int existingIndent = paragraph.length() - trimmed.length();
            String indentStr = " ".repeat(existingIndent);

            if (trimmed.isEmpty()) {
                continue;
            }

            StringBuilder line = new StringBuilder(indentStr);
            String[] tokens = trimmed.split("\\s+");

            for (String token : tokens) {
                if (line.length() > existingIndent && line.length() + token.length() >= effectiveLineLength) {
                    result.append(line.toString().trim()).append("\n").append(" ".repeat(baseIndent)).append(indentStr);
                    line = new StringBuilder(indentStr);
                }
                line.append(token).append(" ");
            }

            if (line.length() > existingIndent) {
                result.append(line.toString().trim());
            }

            if (paragraph.endsWith("\n")) {
                result.append("\n");
            }
        }

        return result.toString();
    }

    String getCurrentPath(Deque<String> pathStack, String property) {
        StringBuilder path = new StringBuilder(property);
        for (String segment : pathStack) {
            path.insert(0, segment + ".");
        }
        return path.toString();
    }
}
