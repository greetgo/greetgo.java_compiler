package kz.greetgo.java_compiler.char_sequence;

import org.testng.annotations.Test;

import static org.fest.assertions.api.Assertions.assertThat;

@Test
public class CharSequenceCompilerTest {

  @Test
  public void compile() throws Exception {

    String src = "package asd.dsa.wow;\n" +
      "\n" +
      "public class HelloWorld implements " + TestInterface.class.getName() + " {\n" +
      "\n" +
      "  @Override\n" +
      "  public String hello() {\n" +
      "    return \"World!!!\";\n" +
      "  }\n" +
      "\n" +
      "  public String by() {\n" +
      "    return \"Hell!!!\";\n" +
      "  }\n" +
      "\n" +
      "}";

    CharSequenceCompiler compiler = new CharSequenceCompiler(getClass().getClassLoader());

    {
      Class<?> aClass = compiler.compile("asd.dsa.wow.HelloWorld", src);
      System.out.println("aClass.getName() = " + aClass.getName());

      TestInterface testInterface = (TestInterface) aClass.newInstance();

      System.out.println("testInterface.hello() = " + testInterface.hello());
      System.out.println("testInterface.by() = " + testInterface.by());

      assertThat(testInterface.hello()).isEqualTo("World!!!");
      assertThat(testInterface.by()).isEqualTo("Hell!!!");
    }

    {
      Class<?> aClass = compiler.classLoader.loadClass("asd.dsa.wow.HelloWorld", true);

      System.out.println("aClass.getName() = " + aClass.getName());

      TestInterface testInterface = (TestInterface) aClass.newInstance();

      System.out.println("testInterface.hello() = " + testInterface.hello());
      System.out.println("testInterface.by() = " + testInterface.by());

      assertThat(testInterface.hello()).isEqualTo("World!!!");
      assertThat(testInterface.by()).isEqualTo("Hell!!!");
    }
  }


}