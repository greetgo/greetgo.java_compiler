package kz.greetgo.java_compiler;

public class JavaCompileError extends RuntimeException {
  public final int exitCode;
  public final String stderr;

  public JavaCompileError(int exitCode, String stderr) {
    super("exitCode = " + exitCode + "\n" + stderr);
    this.exitCode = exitCode;
    this.stderr = stderr;
  }
}
