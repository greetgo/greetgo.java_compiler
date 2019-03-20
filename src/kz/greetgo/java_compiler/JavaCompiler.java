package kz.greetgo.java_compiler;

import java.io.File;
import java.util.List;

public interface JavaCompiler {
  List<File> classpath();

  void compile(File fileJava) throws JavaCompileError;

  void compile(String fileNameJava) throws JavaCompileError;

  void multiCompile(File... files) throws JavaCompileError;
}
