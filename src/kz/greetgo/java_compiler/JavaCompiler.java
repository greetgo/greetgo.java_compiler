package kz.greetgo.java_compiler;

import java.io.File;

public interface JavaCompiler {
  void compile(File fileJava) throws JavaCompileError;

  void compile(String fileNameJava) throws JavaCompileError;

  void multiCompile(File... files) throws JavaCompileError;
}
