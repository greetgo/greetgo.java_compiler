package kz.greetgo.java_compiler;

import kz.greetgo.util.ServerUtil;

import javax.tools.ToolProvider;
import java.io.*;
import java.util.ArrayList;
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
    if (addedToClassPath.isEmpty()) {
      RunHelper h = new RunHelper();
      h.check(compiler.run(h.in(), h.out(), h.err(), fileJava.getPath()));
    } else {
      StringBuilder cp = new StringBuilder();
      for (File file : addedToClassPath) {
        cp.append(file.getPath()).append(File.pathSeparatorChar);
      }
      cp.append(System.getProperty("java.class.path"));
      RunHelper h = new RunHelper();
      h.check(compiler.run(h.in(), h.out(), h.err(), "-classpath", cp.toString(), fileJava.getPath()));
    }

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
    if (addedToClassPath.size() > 0) {
      StringBuilder cp = new StringBuilder();
      for (File file : addedToClassPath) {
        cp.append(file.getPath()).append(File.pathSeparatorChar);
      }
      cp.append(System.getProperty("java.class.path"));
      args.add("-classpath");
      args.add(cp.toString());
    }

    for (File file : files) {
      args.add(file.getPath());
    }

    RunHelper h = new RunHelper();
    h.check(compiler.run(h.in(), h.out(), h.err(), args.toArray(new String[args.size()])));
  }

  @Override
  public void compile(String fileNameJava) throws JavaCompileError {
    compile(new File(fileNameJava));
  }
}
