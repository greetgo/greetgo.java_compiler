package kz.greetgo.java_compiler;

import kz.greetgo.util.RND;
import org.testng.annotations.Test;

import java.io.File;
import java.io.PrintWriter;

import static kz.greetgo.util.ServerUtil.*;
import static org.fest.assertions.api.Assertions.assertThat;

public class JavaCompilerTest {

  @Test
  public void compile_oneDirection() throws Exception {
    String srcDir = "build/src/DefaultCompilerTest_compile_oneDirection_" + RND.intStr(5);

    final File classAFile = new File(srcDir + "/kz/greetgo/java_compiler/gen/ClassA_87162.java");
    final File classBFile = new File(srcDir + "/kz/greetgo/java_compiler/gen/ClassB_87162.java");

    dummyCheck(classAFile.getParentFile().mkdirs());
    dummyCheck(classBFile.getParentFile().mkdirs());

    try (final PrintWriter writer = new PrintWriter(classAFile, "UTF-8")) {
      writer.println("package kz.greetgo.java_compiler.gen;");
      writer.println();
      writer.println("public class ClassA_87162 {");
      writer.println("  public ClassB_87162 classB;");
      writer.println("  public String hello() {");
      writer.println("    return \"Hello from ClassA: \" + classB.hello();");
      writer.println("  }");
      writer.println("}");
    }

    try (final PrintWriter writer = new PrintWriter(classBFile, "UTF-8")) {
      writer.println("package kz.greetgo.java_compiler.gen;");
      writer.println();
      writer.println("public class ClassB_87162 {");
      writer.println("  public String hello() {");
      writer.println("    return \"Hello from ClassB\";");
      writer.println("  }");
      writer.println("}");
    }

    final JavaCompiler compiler = new DefaultCompiler();

    //
    //
    compiler.compile(classBFile);
    //
    //

    addToClasspath(srcDir);

    //
    //
    compiler.compile(classAFile);
    //
    //

    final Class<?> classA = Class.forName("kz.greetgo.java_compiler.gen.ClassA_87162");
    final Object classAInstance = classA.newInstance();

    final Class<?> classB = Class.forName("kz.greetgo.java_compiler.gen.ClassB_87162");
    final Object classBInstance = classB.newInstance();

    classA.getField("classB").set(classAInstance, classBInstance);

    final String hello = (String) classA.getMethod("hello").invoke(classAInstance);
    assertThat(hello).isEqualTo("Hello from ClassA: Hello from ClassB");

    deleteRecursively(srcDir);
  }

  @Test
  public void multiCompile_eachOtherDirected() throws Exception {

    String srcDir = "build/src/DefaultCompilerTest_multiCompile_eachOtherDirected_" + RND.intStr(5);

    final File classAFile = new File(srcDir + "/kz/greetgo/java_compiler/gen/ClassA_1756.java");
    final File classBFile = new File(srcDir + "/kz/greetgo/java_compiler/gen/ClassB_1756.java");

    dummyCheck(classAFile.getParentFile().mkdirs());
    dummyCheck(classBFile.getParentFile().mkdirs());

    try (final PrintWriter writer = new PrintWriter(classAFile, "UTF-8")) {
      writer.println("package kz.greetgo.java_compiler.gen;");
      writer.println();
      writer.println("public class ClassA_1756 {");
      writer.println("  public ClassB_1756 classB, secondClassB;");
      writer.println("  public String hello() {");
      writer.println("    return \"Hello from ClassA \" + classB.hello() + ' ' + secondClassB.secondHello();");
      writer.println("  }");
      writer.println("}");
    }

    try (final PrintWriter writer = new PrintWriter(classBFile, "UTF-8")) {
      writer.println("package kz.greetgo.java_compiler.gen;");
      writer.println();
      writer.println("public class ClassB_1756 {");
      writer.println("  public ClassA_1756 classA;");
      writer.println("  public String hello() {");
      writer.println("    return \"Hello from ClassB\";");
      writer.println("  }");
      writer.println("  public String secondHello() {");
      writer.println("    return \"Second hello from ClassB\";");
      writer.println("  }");
      writer.println("}");
    }

    final JavaCompiler compiler = new DefaultCompiler();

    //
    //
    compiler.multiCompile(classAFile, classBFile);
    //
    //

    addToClasspath(srcDir);

    final Class<?> classA = Class.forName("kz.greetgo.java_compiler.gen.ClassA_1756");
    final Object classAInstance = classA.newInstance();

    final Class<?> classB = Class.forName("kz.greetgo.java_compiler.gen.ClassB_1756");
    final Object classBInstance = classB.newInstance();
    final Object classBSecondInstance = classB.newInstance();

    classA.getField("classB").set(classAInstance, classBInstance);
    classA.getField("secondClassB").set(classAInstance, classBSecondInstance);

    final String hello = (String) classA.getMethod("hello").invoke(classAInstance);
    assertThat(hello).isEqualTo("Hello from ClassA Hello from ClassB Second hello from ClassB");

    deleteRecursively(srcDir);
  }

  @Test
  public void multiCompile_twoSources() throws Exception {

    final String rnd = RND.intStr(5);
    String srcDir1 = "build/src/DefaultCompilerTest_multiCompile_twoSources_" + rnd + "_1";
    String srcDir2 = "build/src/DefaultCompilerTest_multiCompile_twoSources_" + rnd + "_2";

    final File classAFile = new File(srcDir1 + "/kz/greetgo/java_compiler/gen/ClassA_28639.java");
    final File classBFile = new File(srcDir2 + "/kz/greetgo/java_compiler/gen/ClassB_28639.java");

    dummyCheck(classAFile.getParentFile().mkdirs());
    dummyCheck(classBFile.getParentFile().mkdirs());

    try (final PrintWriter writer = new PrintWriter(classAFile, "UTF-8")) {
      writer.println("package kz.greetgo.java_compiler.gen;");
      writer.println();
      writer.println("public class ClassA_28639 {");
      writer.println("  public ClassB_28639 classB, secondClassB;");
      writer.println("  public String hello() {");
      writer.println("    return \"Hello from ClassA \" + classB.hello() + ' ' + secondClassB.secondHello();");
      writer.println("  }");
      writer.println("}");
    }

    try (final PrintWriter writer = new PrintWriter(classBFile, "UTF-8")) {
      writer.println("package kz.greetgo.java_compiler.gen;");
      writer.println();
      writer.println("public class ClassB_28639 {");
      writer.println("  public ClassA_28639 classA;");
      writer.println("  public String hello() {");
      writer.println("    return \"Hello from ClassB\";");
      writer.println("  }");
      writer.println("  public String secondHello() {");
      writer.println("    return \"Second hello from ClassB\";");
      writer.println("  }");
      writer.println("}");
    }

    addToClasspath(srcDir1);
    addToClasspath(srcDir2);

    final JavaCompiler compiler = new DefaultCompiler();

    //
    //
    compiler.multiCompile(classAFile, classBFile);
    //
    //

    final Class<?> classA = Class.forName("kz.greetgo.java_compiler.gen.ClassA_28639");
    final Object classAInstance = classA.newInstance();

    final Class<?> classB = Class.forName("kz.greetgo.java_compiler.gen.ClassB_28639");
    final Object classBInstance = classB.newInstance();
    final Object classBSecondInstance = classB.newInstance();

    classA.getField("classB").set(classAInstance, classBInstance);
    classA.getField("secondClassB").set(classAInstance, classBSecondInstance);

    final String hello = (String) classA.getMethod("hello").invoke(classAInstance);
    assertThat(hello).isEqualTo("Hello from ClassA Hello from ClassB Second hello from ClassB");

    deleteRecursively(srcDir1);
    deleteRecursively(srcDir2);
  }

  @Test
  public void multiCompile_threeSources() throws Exception {

    final String rnd = RND.intStr(5);
    String srcDir1 = "build/src/DefaultCompilerTest_multiCompile_twoSources_" + rnd + "_1";
    String srcDir2 = "build/src/DefaultCompilerTest_multiCompile_twoSources_" + rnd + "_2";
    String srcDir3 = "build/src/DefaultCompilerTest_multiCompile_twoSources_" + rnd + "_3";

    final File classAFile = new File(srcDir1 + "/kz/greetgo/java_compiler/gen/ClassA_918267.java");
    final File classBFile = new File(srcDir2 + "/kz/greetgo/java_compiler/gen/ClassB_918267.java");
    final File classCFile = new File(srcDir3 + "/kz/greetgo/java_compiler/gen/ClassC_918267.java");

    dummyCheck(classAFile.getParentFile().mkdirs());
    dummyCheck(classBFile.getParentFile().mkdirs());
    dummyCheck(classCFile.getParentFile().mkdirs());

    try (final PrintWriter writer = new PrintWriter(classAFile, "UTF-8")) {
      writer.println("package kz.greetgo.java_compiler.gen;");
      writer.println();
      writer.println("public class ClassA_918267 {");
      writer.println("  public ClassB_918267 classB, secondClassB;");
      writer.println("  public String hello() {");
      writer.println("    return \"Hello from ClassA \" + classB.hello() + ' ' + secondClassB.secondHello();");
      writer.println("  }");
      writer.println("}");
    }

    try (final PrintWriter writer = new PrintWriter(classBFile, "UTF-8")) {
      writer.println("package kz.greetgo.java_compiler.gen;");
      writer.println();
      writer.println("public class ClassB_918267 {");
      writer.println("  public ClassA_918267 classA;");
      writer.println("  public ClassC_918267 classC;");
      writer.println("  public String hello() {");
      writer.println("    return \"Hello from ClassB (\" + classC.hello() + \")\";");
      writer.println("  }");
      writer.println("  public String secondHello() {");
      writer.println("    return \"Second hello from ClassB (\" + classC.hello() + \")\";");
      writer.println("  }");
      writer.println("}");
    }

    try (final PrintWriter writer = new PrintWriter(classCFile, "UTF-8")) {
      writer.println("package kz.greetgo.java_compiler.gen;");
      writer.println();
      writer.println("public class ClassC_918267 {");
      writer.println("  public String hello() {");
      writer.println("    return \"Hello from ClassC\";");
      writer.println("  }");
      writer.println("}");
    }

    final JavaCompiler compiler = new DefaultCompiler();

    //
    //
    compiler.multiCompile(classCFile);
    //
    //

    addToClasspath(srcDir1);
    addToClasspath(srcDir2);
    addToClasspath(srcDir3);

    //
    //
    compiler.multiCompile(classAFile, classBFile);
    //
    //

    final Class<?> classA = Class.forName("kz.greetgo.java_compiler.gen.ClassA_918267");
    final Object classAInstance = classA.newInstance();

    final Class<?> classB = Class.forName("kz.greetgo.java_compiler.gen.ClassB_918267");
    final Object classBInstance = classB.newInstance();
    final Object classBSecondInstance = classB.newInstance();

    final Class<?> classC = Class.forName("kz.greetgo.java_compiler.gen.ClassC_918267");
    final Object classCInstance = classC.newInstance();

    classA.getField("classB").set(classAInstance, classBInstance);
    classA.getField("secondClassB").set(classAInstance, classBSecondInstance);
    classB.getField("classC").set(classBInstance, classCInstance);
    classB.getField("classC").set(classBSecondInstance, classCInstance);

    final String hello = (String) classA.getMethod("hello").invoke(classAInstance);
    assertThat(hello).isEqualTo("Hello from ClassA Hello from ClassB (Hello from ClassC)" +
      " Second hello from ClassB (Hello from ClassC)");

    deleteRecursively(srcDir1);
    deleteRecursively(srcDir2);
    deleteRecursively(srcDir3);
  }

  @Test(expectedExceptions = JavaCompileError.class)
  public void compile_error() throws Exception {

    final String rnd = RND.intStr(5);
    String srcDir = "build/src/DefaultCompilerTest_compile_error_" + rnd;

    final File classAFile = new File(srcDir + "/kz/greetgo/java_compiler/gen/ClassA_172654.java");

    dummyCheck(classAFile.getParentFile().mkdirs());

    try (final PrintWriter writer = new PrintWriter(classAFile, "UTF-8")) {
      writer.println("package kz.greetgo.java_compiler.gen;");
      writer.println();
      writer.println("public class ClassA_172654 {");
      writer.println("  public HelloClass helloClass;");
      writer.println("}");
    }

    final JavaCompiler compiler = new DefaultCompiler();

    try {

      //
      //
      compiler.multiCompile(classAFile);
      //
      //

    } finally {
      deleteRecursively(srcDir);
    }

  }


}