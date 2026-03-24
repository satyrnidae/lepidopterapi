package dev.satyrn.lepidoptera.config.serializers;

import dev.satyrn.lepidoptera.api.config.serializers.YamlComment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("ALL")
class YamlCommentInjectorTest {

    private YamlCommentInjector injector;

    @BeforeEach
    void setUp() {
        injector = new YamlCommentInjector(80);
    }

    // -------------------------------------------------------------------------
    // getCurrentPath
    // -------------------------------------------------------------------------

    @Nested
    class GetCurrentPath {
        @Test
        void emptyStack_returnsPropertyOnly() {
            Deque<String> stack = new ArrayDeque<>();
            assertEquals("foo", injector.getCurrentPath(stack, "foo"));
        }

        @Test
        void singleSegmentStack_prefixesCorrectly() {
            Deque<String> stack = new ArrayDeque<>();
            stack.push("parent");
            assertEquals("parent.child", injector.getCurrentPath(stack, "child"));
        }

        @Test
        void multiSegmentStack_buildsFullPath() {
            Deque<String> stack = new ArrayDeque<>();
            // push outermost first so innermost ends up at the head (matching injectComments usage)
            stack.push("a");
            stack.push("b");
            stack.push("c");
            // stack head is "c" (innermost); getCurrentPath iterates head-to-tail and inserts at front
            assertEquals("a.b.c.leaf", injector.getCurrentPath(stack, "leaf"));
        }
    }

    // -------------------------------------------------------------------------
    // wordWrap
    // -------------------------------------------------------------------------

    @Nested
    class WordWrap {
        @Test
        void emptyString_returnsEmpty() {
            assertEquals("", injector.wordWrap(""));
        }

        @Test
        void shortText_notWrapped() {
            String result = injector.wordWrap("Hello world");
            assertEquals("Hello world", result);
        }

        @Test
        void longText_wrapsAtLineLength() {
            // injector line length = 80; COMMENT_PREFIX = "# " (2 chars)
            // effective = max(60, 80) - 2 = 78 chars per line
            String word = "word";
            StringBuilder input = new StringBuilder();
            // Build a sentence > 78 chars
            for (int i = 0; i < 25; i++) {
                if (!input.isEmpty()) input.append(" ");
                input.append(word);
            }
            String result = injector.wordWrap(input.toString());
            // Each line must be <= effectiveLineLength (78)
            for (String line : result.split("\n")) {
                assertTrue(line.length() <= 78, "Line too long: '" + line + "'");
            }
            // Wrapped text must contain all original words
            assertEquals(input.toString().replaceAll("\\s+", " "),
                    result.replaceAll("\\r?\\n", " ").replaceAll("\\s+", " ").trim());
        }

        @Test
        void singleTokenLongerThanLineLength_notSplit() {
            // Tokens longer than line length should not be split mid-token
            String longToken = "a".repeat(200);
            String result = injector.wordWrap(longToken);
            assertTrue(result.contains(longToken), "Long token must appear intact");
        }

        @Test
        void paragraphsPreserved() {
            String text = "First paragraph.\nSecond paragraph.";
            String result = injector.wordWrap(text);
            assertTrue(result.contains("\n"), "Newline between paragraphs must be preserved");
            assertTrue(result.contains("First paragraph."));
            assertTrue(result.contains("Second paragraph."));
        }

        @Test
        void baseIndentAndPreIndentReduceEffectiveLength() {
            // With baseIndent=20 and preIndent=20, effectiveLineLength = max(60, 80-20-20) - 2 = 58
            // wordWrap prepends " ".repeat(baseIndent) on continuation lines, so total line length
            // may exceed 58; check the content portion (stripped of leading whitespace) fits.
            YamlCommentInjector narrow = new YamlCommentInjector(80);
            String word = "word";
            StringBuilder input = new StringBuilder();
            for (int i = 0; i < 20; i++) {
                if (!input.isEmpty()) input.append(" ");
                input.append(word);
            }
            String result = narrow.wordWrap(input.toString(), 20, 20);
            String[] lines = result.split("\n");
            assertTrue(lines.length > 1, "Expected wrapping to occur");
            for (String line : lines) {
                assertTrue(line.stripLeading().length() <= 58,
                        "Content exceeds effectiveLineLength on line: '" + line + "'");
            }
        }
    }

    // -------------------------------------------------------------------------
    // getEnumComments
    // -------------------------------------------------------------------------

    @Nested
    class GetEnumComments {
        enum SimpleEnum { ALPHA, BETA, GAMMA }

        enum AnnotatedEnum {
            @YamlComment("First option")
            ONE,
            @YamlComment("Second option")
            TWO
        }

        @Test
        void nonEnumClass_returnsEmpty() {
            assertEquals("", injector.getEnumComments(String.class, 0));
        }

        @Test
        void simpleEnum_listsAllConstants() {
            String result = injector.getEnumComments(SimpleEnum.class, 0);
            assertFalse(result.isEmpty());
            assertTrue(result.contains("ALPHA"));
            assertTrue(result.contains("BETA"));
            assertTrue(result.contains("GAMMA"));
        }

        @Test
        void simpleEnum_hasValidValuesLeader() {
            String result = injector.getEnumComments(SimpleEnum.class, 0);
            assertTrue(result.startsWith(YamlComment.VALID_VALUES_LEADER));
        }

        @Test
        void annotatedEnum_includesDescriptions() {
            String result = injector.getEnumComments(AnnotatedEnum.class, 0);
            assertTrue(result.contains("First option"));
            assertTrue(result.contains("Second option"));
        }
    }

    // -------------------------------------------------------------------------
    // buildNestedCommentMap
    // -------------------------------------------------------------------------

    @Nested
    class BuildNestedCommentMap {

        // A minimal bean: public no-arg constructor + getters
        public static class SimpleConfig {
            private String name = "";
            private int count = 0;

            public SimpleConfig() {}

            @YamlComment("The name value")
            public String getName() { return name; }
            public void setName(String name) { this.name = name; }

            @YamlComment("The count value")
            public int getCount() { return count; }
            public void setCount(int count) { this.count = count; }
        }

        public static class SectionConfig {
            private Inner inner = new Inner();

            public SectionConfig() {}

            @YamlComment(value = "Inner section", sectionHeader = true)
            public Inner getInner() { return inner; }
            public void setInner(Inner inner) { this.inner = inner; }

            public static class Inner {
                private boolean flag = false;
                public Inner() {}
                @YamlComment("A boolean flag")
                public boolean isFlag() { return flag; }
                public void setFlag(boolean flag) { this.flag = flag; }
            }
        }

        @Test
        void simpleBean_mapContainsExpectedKeys() throws Exception {
            Map<String, String> map = injector.buildNestedCommentMap(SimpleConfig.class);
            assertTrue(map.containsKey("name"), "Expected key 'name'");
            assertTrue(map.containsKey("count"), "Expected key 'count'");
        }

        @Test
        void simpleBean_commentValuesPresent() throws Exception {
            Map<String, String> map = injector.buildNestedCommentMap(SimpleConfig.class);
            assertTrue(map.get("name").contains("The name value"));
            assertTrue(map.get("count").contains("The count value"));
        }

        @Test
        void sectionHeader_storedAtSectionPath() throws Exception {
            Map<String, String> map = injector.buildNestedCommentMap(SectionConfig.class);
            String sectionKey = "inner." + YamlCommentInjector.SECTION_IDENTIFIER;
            assertTrue(map.containsKey(sectionKey), "Expected section key: " + sectionKey);
            assertTrue(map.get(sectionKey).contains("Inner section"));
        }

        @Test
        void nestedBeanProperty_hasNestedKey() throws Exception {
            Map<String, String> map = injector.buildNestedCommentMap(SectionConfig.class);
            assertTrue(map.containsKey("inner.flag"), "Expected nested key 'inner.flag'");
        }

        @Test
        void nonBeanClass_returnsEmptyMap() throws Exception {
            // String is not a bean per BeanInspection
            Map<String, String> map = injector.buildNestedCommentMap(String.class);
            assertTrue(map.isEmpty());
        }
    }

    // -------------------------------------------------------------------------
    // injectComments
    // -------------------------------------------------------------------------

    @Nested
    class InjectComments {

        @Test
        void emptyYaml_returnsEmpty() {
            String result = injector.injectComments("", Map.of(), null);
            assertEquals("", result);
        }

        @Test
        void yamlWithoutMatchingKeys_unchanged() {
            String yaml = "foo: bar\nbaz: 42\n";
            String result = injector.injectComments(yaml, Map.of(), null);
            // Comments map is empty - output should contain same keys/values
            assertTrue(result.contains("foo: bar"));
            assertTrue(result.contains("baz: 42"));
        }

        @Test
        void commentInjectedBeforeMatchingProperty() {
            String yaml = "name: hello\n";
            Map<String, String> comments = Map.of("name", "The name");
            String result = injector.injectComments(yaml, comments, null);
            int commentIdx = result.indexOf("# The name");
            int propIdx = result.indexOf("name: hello");
            assertTrue(commentIdx >= 0, "Comment must be present");
            assertTrue(commentIdx < propIdx, "Comment must precede property");
        }

        @Test
        void sectionCommentInjectedBeforeSectionKey() {
            String yaml = "inner:\n    flag: true\n";
            Map<String, String> comments = Map.of(
                    "inner." + YamlCommentInjector.SECTION_IDENTIFIER, "Inner section header"
            );
            String result = injector.injectComments(yaml, comments, null);
            int commentIdx = result.indexOf("# Inner section header");
            int sectionIdx = result.indexOf("inner:");
            assertTrue(commentIdx >= 0, "Section comment must be present");
            assertTrue(commentIdx < sectionIdx, "Section comment must precede section key");
        }

        @Test
        void yamlDirectivesStripped() {
            String yaml = "!!dev.example.Config\nfoo: bar\n";
            String result = injector.injectComments(yaml, Map.of(), null);
            assertFalse(result.contains("!!"), "YAML directives must be stripped");
            assertTrue(result.contains("foo: bar"));
        }

        @Test
        void classLevelComment_prependedAsHeader() {
            @YamlComment("Top-level file comment")
            class AnnotatedConfig {}

            String yaml = "foo: bar\n";
            String result = injector.injectComments(yaml, Map.of(), AnnotatedConfig.class);
            int commentIdx = result.indexOf("# Top-level file comment");
            int propIdx = result.indexOf("foo: bar");
            assertTrue(commentIdx >= 0, "Class-level comment must appear");
            assertTrue(commentIdx < propIdx, "Class-level comment must be before content");
        }

        @Test
        void nestedPropertyComment_indentedCorrectly() {
            // 4-space indent for nested property
            String yaml = "outer:\n    inner: value\n";
            Map<String, String> comments = Map.of("outer.inner", "Nested comment");
            String result = injector.injectComments(yaml, comments, null);
            // The comment line for a nested property should be indented
            String[] lines = result.split("\n");
            boolean found = false;
            for (String line : lines) {
                if (line.contains("# Nested comment")) {
                    found = true;
                    assertTrue(line.startsWith("    "), "Nested comment must be indented: '" + line + "'");
                }
            }
            assertTrue(found, "Nested comment must be present");
        }

        @Test
        void multiLineComment_allLinesInjected() {
            String yaml = "foo: bar\n";
            Map<String, String> comments = Map.of("foo", "Line one\nLine two\nLine three");
            String result = injector.injectComments(yaml, comments, null);
            assertTrue(result.contains("# Line one"));
            assertTrue(result.contains("# Line two"));
            assertTrue(result.contains("# Line three"));
        }
    }
}
