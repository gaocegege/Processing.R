package rprocessing.lancher;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;

import processing.core.PApplet;
import rprocessing.RunnableSketch;
import rprocessing.Runner;
import rprocessing.util.RScriptReader;

/**
 * StandaloneSketch
 * 
 * @author github.com/gaocegege
 */
public class StandaloneSketch implements RunnableSketch {

  private static final boolean VERBOSE = Boolean.parseBoolean(System.getenv("VERBOSE_RLANG_MODE"));

  private final File sketchPath;
  private final String code;

  // private final List<File> libraryDirs;

  @SuppressWarnings("unused")
  private static void log(final Object... objs) {
    if (!VERBOSE) {
      return;
    }
    for (final Object o : objs) {
      System.err.print(String.valueOf(o));
    }
    System.err.println();
  }

  /**
   * Returns the path of the main processing-py.jar file.
   * 
   * Used from launcher
   * 
   * @return the path of processing-py.jar.
   */
  public File getMainJarFile() {
    // On a Mac, when launched as an app, this will contain ".app/Contents/Java/processing-py.jar"
    try {
      return new File(Runner.class.getProtectionDomain().getCodeSource().getLocation().toURI());
    } catch (final URISyntaxException exception) {
      exception.printStackTrace();
    }
    return null;
  }

  /**
   * Returns the 'root' folder of this instance. Used when running with a wrapper.
   * 
   * @return the distribution root directory.
   */
  public File getRuntimeRoot() {
    final File jar = getMainJarFile();

    // If we are on a mac
    if (jar.getAbsolutePath().contains(".app/Contents/")) {
      return jar.getParentFile().getParentFile();
    }

    // If we are on Windows
    return jar.getParentFile();
  }

  public StandaloneSketch(final String[] args) throws Exception {
    final List<String> argsList = Arrays.asList(args);

    // In case the sketch path points to "internal" we get it from the wrapper
    if (argsList.contains("--internal")) {
      this.sketchPath = new File(getRuntimeRoot(), "Runtime/sketch.R").getAbsoluteFile();
    } else {
      this.sketchPath = new File(args[args.length - 1]).getAbsoluteFile();
    }

    // Debug when using launcher
    if (argsList.contains("--redirect")) {
      // Get sketch path, name, out and err names
      final String name = sketchPath.getName().replaceAll("\\.R", "");
      final String out = sketchPath.getParent() + "/" + name + ".out.txt";
      final String err = sketchPath.getParent() + "/" + name + ".err.txt";

      // Redirect actual input
      System.setOut(new PrintStream(new FileOutputStream(out)));
      System.setErr(new PrintStream(new FileOutputStream(err)));
    }

    // Read main code in
    this.code = RScriptReader.readText(sketchPath.toPath());

    // populate library directories
    // {
    // this.libraryDirs = new ArrayList<>();
    // final String BUILD_PROPERTIES = "build.properties";
    // final String propsResource;
    // try {
    // propsResource = URLDecoder.decode(Runner.class.getResource(BUILD_PROPERTIES)
    // .toString(), "UTF-8");
    // } catch (final UnsupportedEncodingException e) {
    // throw new RuntimeException("Impossible: " + e);
    // }
    //
    // final Pattern JAR_RESOURCE = Pattern
    // .compile("jar:file:(.+?)/processing-py\\.jar!/jycessing/"
    // + Pattern.quote(BUILD_PROPERTIES));
    // final Pattern FILE_RESOURCE = Pattern.compile("file:(.+?)/bin/jycessing/"
    // + Pattern.quote(BUILD_PROPERTIES));
    //
    // final Matcher jarMatcher = JAR_RESOURCE.matcher(propsResource);
    // final Matcher fileMatcher = FILE_RESOURCE.matcher(propsResource);
    // if (jarMatcher.matches()) {
    // log("We're running from a JAR file.");
    // libraryDirs.add(new File(jarMatcher.group(1), "libraries"));
    // } else if (fileMatcher.matches()) {
    // log("We're running from class files.");
    // libraryDirs.add(new File(fileMatcher.group(1), "libraries"));
    // } else {
    // log("WARNING: I can't find my libraries directory!");
    // libraryDirs.add(new File("libraries"));
    // }
    // }
  }

  @Override
  public File getMainFile() {
    return sketchPath;
  }

  @Override
  public File getHomeDirectory() {
    return sketchPath.getParentFile();
  }

  @Override
  public String[] getPAppletArguments() {
    return new String[] {
        PApplet.ARGS_SKETCH_FOLDER + "=" + sketchPath.getParentFile().getAbsolutePath(),
        sketchPath.getName() // must be last argument
    };
  }

  // @Override
  // public List<File> getLibraryDirectories() {
  // return libraryDirs;
  // }

  // @Override
  // public LibraryPolicy getLibraryPolicy() {
  // return LibraryPolicy.PROMISCUOUS;
  // }

  @Override
  public String getMainCode() {
    return code;
  }

  @Override
  public boolean shouldRun() {
    return true;
  }

  // @Override
  // public List<File> getPathEntries() {
  // final List<File> entries = new ArrayList<>();
  //
  // // Main sketch folder
  // entries.add(sketchPath.getParentFile());
  //
  // for (final File dir : getLibraryDirectories()) {
  // // In case the user has added python libraries in their library directories
  // entries.add(dir);
  // }
  //
  // return entries;
  // }

}
