package kz.greetgo.java_compiler;

import kz.greetgo.util.ServerUtil;

import javax.tools.ToolProvider;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

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

  @Override
  public void compile(File fileJava) throws JavaCompileError {

    final Set<File> addedToClassPath = ServerUtil.getAddedToClassPath();

    List<String> cp = new ArrayList<>();
    for (File file : addedToClassPath) {
      cp.add(file.getPath());
    }

    cp.addAll(Arrays.asList(System.getProperty("java.class.path").split(File.pathSeparator)));

    RunHelper h = new RunHelper();
    h.check(compiler.run(h.in(), h.out(), h.err(),
      "-classpath", String.join(File.pathSeparator, cp),
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

    final Set<File> addedToClassPath = ServerUtil.getAddedToClassPath();
    {
      StringBuilder cp = new StringBuilder();
      for (File file : addedToClassPath) {
        cp.append(file.getPath()).append(File.pathSeparatorChar);
      }
      cp.append(System.getProperty("java.class.path"));

      String cpStr = cp.toString();
      if (cpStr.startsWith(File.pathSeparator)) {
        cpStr = cpStr.substring(1);
      }
      args.add("-classpath");
      args.add(cpStr);
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
