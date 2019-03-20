package kz.greetgo.java_compiler;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Collections.unmodifiableList;

public class FilesClassLoader extends URLClassLoader {

  public FilesClassLoader(ClassLoader parent) {
    super(new URL[0], parent);
  }

  private final ConcurrentHashMap<File, File> fileSet = new ConcurrentHashMap<>();

  public void addFile(File file) {
    if (!file.exists()) {
      throw new IllegalArgumentException("File " + file + " does not exist");
    }

    if (fileSet.containsKey(file)) {
      return;
    }

    try {
      addURL(file.toURI().toURL());
    } catch (MalformedURLException e) {
      throw new RuntimeException(e);
    }

    fileSet.put(file, file);
  }

  public List<File> getFiles() {
    return unmodifiableList(new ArrayList<>(fileSet.values()));
  }
}
