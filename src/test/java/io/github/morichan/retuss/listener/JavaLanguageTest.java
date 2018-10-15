package io.github.morichan.retuss.listener;

import io.github.morichan.retuss.language.java.Java;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JavaLanguageTest {
    JavaLanguage obj;

    @Nested
    class 正しいソースコードの場合 {

        @BeforeEach
        void setup() {
            obj = new JavaLanguage();
        }

        @Test
        void クラス名を抽出する() {
            String code = "class ClassName {}";
            String expected = "ClassName";

            obj.parseForClassDiagram(code);
            String actual = obj.getJava().getClasses().get(0).getName();

            assertThat(actual).isEqualTo(expected);
        }

        @Test
        void 継承先クラス名を抽出する() {
            String code = "class ClassName extends ExtendedClassName {}";
            String expected = "ExtendedClassName";

            obj.parseForClassDiagram(code);
            String actual = obj.getJava().getClasses().get(0).getExtendsClassName();

            assertThat(actual).isEqualTo(expected);
        }

        @Test
        void ドットを挟んだ継承先クラス名を抽出する() {
            String code = "class ClassName extends Extended.ClassName {}";
            String expected = "Extended.ClassName";

            obj.parseForClassDiagram(code);
            String actual = obj.getJava().getClasses().get(0).getExtendsClassName();

            assertThat(actual).isEqualTo(expected);
        }
    }

    @Nested
    class Javaインスタンスの場合 {

        @BeforeEach
        void setup() {
            obj = new JavaLanguage();
        }

        @Test
        void クラス名を持つクラスを1つ抽出する() {
            String expected = "class ClassName {\n}\n";

            obj.parseForClassDiagram("class ClassName {\n}\n");
            Java actual = obj.getJava();

            assertThat(actual.getClasses().get(0)).hasToString(expected);
        }
    }
}