package dev.satyrn.lepidoptera.config.serializers;

import com.google.common.collect.Maps;
import dev.satyrn.lepidoptera.LepidopteraAPI;
import dev.satyrn.lepidoptera.annotations.Api;
import dev.satyrn.lepidoptera.annotations.YamlComment;
import dev.satyrn.lepidoptera.beans.BeanInspection;
import dev.satyrn.lepidoptera.mixin.accessors.me.shedaniel.autoconfig.serializer.YamlConfigSerializerAccessor;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.serializer.YamlConfigSerializer;
import me.shedaniel.cloth.clothconfig.shadowed.org.yaml.snakeyaml.DumperOptions;
import me.shedaniel.cloth.clothconfig.shadowed.org.yaml.snakeyaml.Yaml;
import me.shedaniel.cloth.clothconfig.shadowed.org.yaml.snakeyaml.constructor.Constructor;
import me.shedaniel.cloth.clothconfig.shadowed.org.yaml.snakeyaml.representer.Representer;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;

@Api
public class CommentedYamlConfigSerializer<T extends ConfigData> extends YamlConfigSerializer<T> {
    public static final int DEFAULT_LINE_LENGTH = 120;
    public static final int MIN_LINE_LENGTH = 60;
    private static final String COMMENT_PREFIX = "# ";
    private static final String ENUM_LIST_BULLET = "- ";
    private static final String YAML_DIRECTIVE_PREFIX = "!!";
    private static final String SECTION_IDENTIFIER = "__SECTION";
    private static final String TYPE_LEADER = "Type: ";
    private static final int INDENT = 4;
    private final YamlConfigSerializerAccessor accessor = (YamlConfigSerializerAccessor) this;
    private final int lineLength;

    public CommentedYamlConfigSerializer(Config definition, Class<T> configClass, int lineLength) {
        super(definition, configClass, getYaml(configClass));
        this.lineLength = lineLength;
    }

    @SuppressWarnings("unused")
    public CommentedYamlConfigSerializer(Config definition, Class<T> configClass) {
        this(definition, configClass, DEFAULT_LINE_LENGTH);
    }

    private static <T> Yaml getYaml(Class<T> configClass) {
        final DumperOptions dumperOptions = getDumperOptions();
        final Representer representer = new Representer(dumperOptions);
        representer.getPropertyUtils().setSkipMissingProperties(true);
        final Constructor constructor = new Constructor(configClass);
        constructor.setPropertyUtils(representer.getPropertyUtils());

        return new Yaml(constructor, representer, dumperOptions);
    }

    private static @NotNull DumperOptions getDumperOptions() {
        final DumperOptions dumperOptions = new DumperOptions();
        dumperOptions.setPrettyFlow(true);
        dumperOptions.setCanonical(false);
        dumperOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        dumperOptions.setNonPrintableStyle(DumperOptions.NonPrintableStyle.ESCAPE);
        dumperOptions.setAllowReadOnlyProperties(false);
        dumperOptions.setIndent(INDENT);
        dumperOptions.setTags(Maps.newHashMap());
        dumperOptions.setExplicitStart(false);
        dumperOptions.setExplicitEnd(false);
        return dumperOptions;
    }

    @Override
    public void serialize(T config) {
        final Path configPath = this.accessor.callGetConfigPath();

        try {
            final Map<String, String> commentMap = buildNestedCommentMap(config.getClass());
            final String yaml = this.injectComments(this.accessor.getYaml().dump(config), commentMap);
            Files.createDirectories(configPath.getParent());
            Files.writeString(configPath, yaml, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (Exception e) {
            LepidopteraAPI.error("Failed to save mod configuration file " + configPath, e);
        }
    }

    @Override
    public T deserialize() {
        Path configPath = this.accessor.callGetConfigPath();
        if (Files.exists(configPath)) {
            try {
                List<String> lines = Files.readAllLines(configPath);

                // Strip YAML Directives from config
                for (var i = 0; i < lines.size(); ++i) {
                    if (lines.get(i).trim().startsWith(YAML_DIRECTIVE_PREFIX)) {
                        lines.remove(i--);
                    }
                }

                return this.accessor.getYaml().load(String.join("\n", lines));
            } catch (Exception e) {
                LepidopteraAPI.error("Failed to load mod configuration file " + configPath, e);
            }
        }
        LepidopteraAPI.warn("The mod configuration for the {} file could not be loaded and will be defaulted!",
                this.accessor.getDefinition().name());
        return this.createDefault();
    }

    private String injectComments(String yaml, Map<String, String> commentMap) {
        List<String> lines = new ArrayList<>(Arrays.asList(yaml.split("\\r?\\n")));
        if (lines.isEmpty()) {
            return yaml;
        }

        Deque<String> pathStack = new ArrayDeque<>();

        // Check config class for a YamlComment
        final @Nullable YamlComment classComment = this.accessor.getConfigClass().getAnnotation(YamlComment.class);
        if (classComment != null) {
            // Skip YAML directive
            int startIndex = lines.get(0).startsWith(YAML_DIRECTIVE_PREFIX) ? 1 : 0;
            lines.add(startIndex, "");

            String[] commentLines = wordWrap(classComment.value()).split("\\r?\\n");
            for (int i = commentLines.length - 1; i >= 0; i--) {
                lines.add(startIndex, COMMENT_PREFIX + commentLines[i]);
            }

            lines.add(startIndex, "");
        }

        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            String trimmed = line.trim();

            // Remove YAML directive
            if (trimmed.startsWith(YAML_DIRECTIVE_PREFIX)) {
                lines.remove(i--);
                continue;
            }
            if (trimmed.isEmpty() || trimmed.startsWith("- ")) {
                continue;
            }

            // Calculate indentation
            int lineIndent = line.indexOf(trimmed.charAt(0));
            int indentLevel = lineIndent / INDENT;

            // Update path stack
            while (indentLevel < pathStack.size()) {
                pathStack.pop();
            }

            // Scan the file for key: value pairs
            if (trimmed.endsWith(":")) {
                String key = trimmed.substring(0, trimmed.length() - 1).trim();

                String fullPath = getCurrentPath(pathStack, key);
                String sectionPath = key + "." + SECTION_IDENTIFIER;
                if (commentMap.containsKey(sectionPath)) {
                    String[] commentLines = commentMap.get(sectionPath).split("\\r?\\n");
                    for (int j = commentLines.length - 1; j >= 0; j--) {
                        lines.add(i, COMMENT_PREFIX + commentLines[j]);
                    }
                    // Add an extra line break between sections
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
                String property = trimmed.split(":")[0].trim();
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

    private Map<String, String> buildNestedCommentMap(Class<?> clazz) throws Exception {
        Map<String, String> comments = new HashMap<>();
        if (BeanInspection.isBean(clazz)) {
            buildCommentsRecursive(clazz, "", comments);
        }
        return comments;
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
                    if (!comment.value().isBlank()) {
                        if (comment.sectionHeader()) {
                            comments.put(fullPath + "." + SECTION_IDENTIFIER, wordWrap(comment.value()));
                        } else {
                            comments.put(fullPath, wordWrap(comment.value(), 0, indentLevel * INDENT));
                        }
                    }

                    if (!comment.note().isEmpty()) {
                        String existing = comments.getOrDefault(fullPath, "");
                        comments.put(fullPath, (existing.isEmpty() ? "" : existing + "\n") +
                                comment.noteLeader() +
                                wordWrap(comment.note(), comment.noteLeader().length(), indentLevel * INDENT));
                    }

                    if (!comment.sectionHeader() && comment.emitType()) {
                        String existing = comments.getOrDefault(fullPath, "");
                        comments.put(fullPath, (existing.isEmpty() ? "" : existing + "\n") +
                                TYPE_LEADER +
                                getter.getReturnType().getName());
                    }

                    if (!comment.defaultValue().isEmpty()) {
                        String existing = comments.getOrDefault(fullPath, "");
                        comments.put(fullPath, (existing.isEmpty() ? "" : existing + "\n") +
                                comment.defaultValueLeader() +
                                wordWrap(comment.defaultValue(), comment.defaultValueLeader().length(),
                                        indentLevel * INDENT));
                    }
                }

                // Handle enum values
                if (prop.getPropertyType().isEnum()) {
                    String enumComments = getEnumComments(prop.getPropertyType(), indentLevel);
                    if (!enumComments.isEmpty()) {
                        String existing = comments.getOrDefault(fullPath, "");
                        comments.put(fullPath, (existing.isEmpty() ? "" : existing + "\n") + enumComments);
                    }
                }

                if (BeanInspection.isBean(prop.getPropertyType()) && (comment == null || comment.emitChildren())) {
                    buildCommentsRecursive(prop.getPropertyType(), fullPath, comments);
                }
            }
        }
    }

    private String getEnumComments(Class<?> enumClass, int indentLevel) {
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

                    // Additional lines indented past the enum name
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

    private String wordWrap(String text) {
        return this.wordWrap(text, 0, 0);
    }

    private String wordWrap(String text, int baseIndent, int preIndent) {
        if (text.isEmpty()) {
            return "";
        }

        // Calculate effective line length
        int minLength = Math.max(MIN_LINE_LENGTH, this.lineLength / 2);
        int effectiveLineLength = Math.max(minLength, this.lineLength - baseIndent - preIndent) -
                COMMENT_PREFIX.length();

        StringBuilder result = new StringBuilder();
        String[] paragraphs = text.split("(?<=\n)", -1); // Keep newlines

        for (String paragraph : paragraphs) {
            if (paragraph.equals("\n")) {
                result.append("\n");
                continue;
            }

            // Detect and preserve existing indentation
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

    private String getCurrentPath(Deque<String> pathStack, String property) {
        StringBuilder path = new StringBuilder(property);
        for (String segment : pathStack) {
            path.insert(0, segment + ".");
        }
        return path.toString();
    }

}
