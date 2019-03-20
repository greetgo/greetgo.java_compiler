package kz.greetgo.java_compiler;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.unmodifiableList;

public class FilesClassLoader extends URLClassLoader {

  public FilesClassLoader(ClassLoader parent) {
    super(new URL[0], parent);
  }

  private final List<File> files = new ArrayList<>();

  public void addFile(File file) {
    if (!file.exists()) {
      throw new IllegalArgumentException("File " + file + " does not exist");
    }

    try {
      addURL(file.toURI().toURL());
    } catch (MalformedURLException e) {
      throw new RuntimeException(e);
    }

    files.add(file);
  }

  public List<File> getFiles() {
    return unmodifiableList(files);
  }
}
