package kz.greetgo.java_compiler;

public class JavaCompilerFactory {
  private JavaCompilerFactory() {
  }

  public static JavaCompiler createDefault() {
    return new DefaultCompiler();
  }
}
