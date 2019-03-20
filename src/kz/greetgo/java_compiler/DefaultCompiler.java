package kz.greetgo.java_compiler;

import javax.tools.ToolProvider;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class DefaultCompiler implements JavaCompiler {

  final javax.tools.JavaCompiler compiler;

  DefaultCompiler() {
    compiler = ToolProvider.getSystemJavaCompiler();
  }

  static class RunHelper {

    final ByteArrayOutputStream err = new ByteArrayOutputStream();

    public InputStream in() {
      return System.in;
    }

    public OutputStream out() {
      return System.out;
    }

    public OutputStream err() {
      return err;
    }

    public void check(int exitCode) {
      if (exitCode == 0) return;
      try {
        throw new JavaCompileError(exitCode, err.toString("UTF-8"));
      } catch (UnsupportedEncodingException e) {
        throw new RuntimeException(e);
      }
    }
  }

  private final List<File> classpath = new ArrayList<>();

  {
    String javaClassPath = System.getProperty("java.class.path");
    if (javaClassPath != null) {
      Arrays.stream(javaClassPath
        .split(File.pathSeparator))
        .map(File::new)
        .filter(File::exists)
        .forEachOrdered(classpath::add);
    }
  }

  @Override
  public List<File> classpath() {
    return classpath;
  }

  private String classpathStr() {
    return classpath().stream().map(File::getPath).collect(Collectors.joining(File.pathSeparator));
  }


  @Override
  public void compile(File fileJava) throws JavaCompileError {
    RunHelper h = new RunHelper();
    h.check(compiler.run(h.in(), h.out(), h.err(),
      "-classpath", classpathStr(),
      fileJava.getPath()));
  }

  @Override
  public void multiCompile(File... files) {
    if (files.length == 0) return;
    if (files.length == 1) {
      compile(files[0]);
      return;
    }

    List<String> args = new ArrayList<>();

    {
      args.add("-classpath");
      args.add(classpathStr());
    }

    for (File file : files) {
      args.add(file.getPath());
    }

    RunHelper h = new RunHelper();
    h.check(compiler.run(h.in(), h.out(), h.err(), args.toArray(new String[0])));
  }

  @Override
  public void compile(String fileNameJava) throws JavaCompileError {
    compile(new File(fileNameJava));
  }
}
