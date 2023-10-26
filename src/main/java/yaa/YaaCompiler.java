package yaa;

import org.codehaus.plexus.util.FileUtils;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import yaa.parser.ParseResult;
import yaa.parser.YaaParser;
import yaa.pojos.GlobalData;
import yaa.pojos.LogData;
import yaa.pojos.YaaCompilerProvider;
import yaa.pojos.YaaError;
import yaa.pojos.jMold.JMold;
import yaa.semantic.passes.fs1.F1;
import yaa.semantic.passes.fs2.F2;
import yaa.semantic.passes.fs3.F3;
import yaa.semantic.passes.fs4.F4;
import yaa.semantic.passes.fs5.F5;
import yaa.semantic.passes.fs6.F6;

import javax.lang.model.SourceVersion;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static java.lang.ClassLoader.getSystemClassLoader;
import static java.lang.System.nanoTime;
import static java.nio.file.Files.exists;
import static java.nio.file.Files.walkFileTree;
import static java.nio.file.Path.of;
import static yaa.Yaa.main$clz$name;
import static yaa.Yaa.main$fun$name;
import static yaa.pojos.GlobalData.*;
import static yaa.pojos.jMold.JMold.yaa_compiler_loader;

public class YaaCompiler {
  public YaaCompiler(YaaCompilerProvider provider) {
    this.provider = provider;
  }

  private final YaaCompilerProvider provider;

  private static int table_size = 0;

  private List<ParseResult> parseYaa() {
    var start_time = nanoTime();
    final List<ParseResult> results = new ArrayList<>();
    try {
      walkFileTree(of(provider.getBasedir() + "/src/main/yaa"), new SimpleFileVisitor<>() {
        private static String fileExtension(Path path) {
          var fileName = path.getFileName().toString();
          if (fileName.length() > 4) {
            return fileName.substring(fileName.length() - 4);
          }
          return fileName;
        }

        @Override
        public FileVisitResult visitFile(Path path, BasicFileAttributes ats) throws IOException {
          if (fileExtension(path).equals(".yaa")) {
            var file_content = Files.readString(path);
            if (file_content.isBlank() || file_content.isEmpty()) {
              addWarnLog("skipping empty source file " + path.getFileName());
              return FileVisitResult.CONTINUE;
            }
            table_size = table_size + 1;
            var name_with_ext = path.getFileName().toString();
            var name = name_with_ext.substring(0, name_with_ext.indexOf("."));
            if (SourceVersion.isKeyword(name)) {
              addErrorLog(
                  path + ": The java keyword \"" + name +
                      "\" can't be used to name source files"
              );
            } else {
              var path_string = path.toString();
              var simplePath = path_string.substring(provider.getBasedir().length() + 14);
              YaaError.filePath = simplePath;
              addInfoLog("parsing " + simplePath);
              if (path.equals(Path.of("src/main/yaa/" + provider.getProjectName() + ".yaa"))) {
                YaaParser.inMainFile = true;
              }
              results.add(new ParseResult(simplePath, new YaaParser(simplePath, file_content).parse()));
            }
          }
          return FileVisitResult.CONTINUE;
        }
      });
    } catch (IOException e) {
      addErrorLog("An IO exception occurred, your folder structure is probably not well structured");
      return results;
    }
    var run_time = (nanoTime() - start_time);
    addInfoLog("parsing of all sources took " + ((run_time / 1000_000_000.0)) + "s");
    addInfoLog("");
    tables4File = new HashMap<>(table_size);
    compiledClasses = new ArrayList<>(table_size);
    return results;
  }

  public LogData compile() {
    try {
      yaa_compiler_loader = getSystemClassLoader();
      if (provider.getArtifacts().size() > 0) {
        var artifacts = provider.getArtifacts();
        var loader_uris = new URL[artifacts.size()];
        for (int i = 0; i < artifacts.size(); i++) {
          try {
            loader_uris[i] = artifacts.get(i).toURI().toURL();
          } catch (MalformedURLException e) {
            addErrorLog(artifacts.get(i) + " is not well formed");
          }
        }
        yaa_compiler_loader = new URLClassLoader(loader_uris, getSystemClassLoader());
      }

      var start_time = nanoTime();
      var parseResults = parseYaa();
      for (var className : langPkgClassNames) {
        //this is done so that java.lang classes can be cached.
        addInfoLog("Preparing java.lang." + className + " for caching");
        new JMold().newClz("java.lang." + className);
      }
      addInfoLog("");
      Yaa.setUpSpecialClasses();
      pass1(parseResults);
      pass2(parseResults);
      pass3(parseResults);
      pass4(parseResults);
      pass5(parseResults);

      if (logData.hasError) {
        return logData;
      }

      pass6(parseResults);

      //clear previously written classes
      if (exists(of(provider.getBasedir() + "/target"))) {
        try {
          FileUtils.cleanDirectory(provider.getBasedir() + "/target");
        } catch (Exception e) {
          GlobalData.addErrorLog("Failed to delete 'target' directory for code generation");
          GlobalData.addErrorLog("This is most often caused when the " +
              "'target' directory is opened by another program");
        }
      }

      //now write the new classes
      for (var compiledClass : GlobalData.compiledClasses) {
        var internalName = compiledClass.internalName;
        var classPath = internalName.substring(0, internalName.lastIndexOf("/"));

        var clzPackage = new File("target/classes" + sp + classPath);
        if (clzPackage.exists() || clzPackage.mkdirs()) {
          var filePath = "target/classes" + sp + internalName + ".class";
          var outputFile = new File(filePath);
          try {
            if (outputFile.createNewFile()) {
              var bos = new BufferedOutputStream(new FileOutputStream(outputFile));
              bos.write(compiledClass.classBytes);
              bos.flush();
            }
          } catch (IOException e) {
            e.printStackTrace();
          }
        }
      }

      var run_time = (nanoTime() - start_time);
      addInfoLog("source compilation successful in " + (run_time / 1000000000.0) + "s");
      if (mainFunction != null) {
        writeRunningPoint();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return logData;
  }

  private void writeRunningPoint() {
    ClassWriter entryCW = new ClassWriter(2);
    entryCW.visit(52, 1, provider.getProjectName() + "/" + provider.getProjectName(), null, "java/lang/Object", new String[0]);
    entryCW.visitSource(provider.getProjectName() + "/" + provider.getProjectName(), null);
    MethodVisitor iw = entryCW.visitMethod(1, "<init>", "()V", null, new String[0]);
    iw.visitCode();
    iw.visitVarInsn(25, 0);
    iw.visitMethodInsn(183, "java/lang/Object", "<init>", "()V", false);
    iw.visitInsn(177);
    iw.visitMaxs(0, 0);
    iw.visitEnd();
    MethodVisitor entryWriter = entryCW.visitMethod(9, "main", "([Ljava/lang/String;)V", null, new String[0]);
    entryWriter.visitCode();
    entryWriter.visitVarInsn(25, 0);
    entryWriter.visitMethodInsn(184, main$clz$name, main$fun$name, "([Ljava/lang/String;)V", false);
    entryWriter.visitInsn(177);
    entryWriter.visitMaxs(0, 0);
    entryWriter.visitEnd();
    if (Files.exists(Path.of("target/classes/" + provider.getProjectName()))) {
      try {
        File file = new File(
            "target/classes/"
                + provider.getProjectName()
                + "/" + provider.getProjectName()
                + ".class"
        );
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
        bos.write(entryCW.toByteArray());
        bos.flush();
      } catch (IOException ioException) {
        addErrorLog("An IO exception occurred while writing entry point");
      }
    }
  }

  private void pass1(List<ParseResult> parseResults) {
    var start_time = nanoTime();
    parseResults.forEach((ps -> {
      addInfoLog("preparing " + ps.path + " for compilation");
      new F1(ps.stmts, ps.path).execute();
    }));
    var run_time = (nanoTime() - start_time);
    addInfoLog("preparation of sources for compilation took " + ((run_time / 1000_000_000.0)) + "s");
  }

  private void pass2(List<ParseResult> parseResults) {
    var start_time = nanoTime();
    parseResults.forEach((ps -> {
      addInfoLog("evaluating the imports defined in " + ps.path);
      new F2(ps.stmts, ps.path).execute();
    }));
    var run_time = (nanoTime() - start_time);
    addInfoLog("import evaluation in all sources took " + ((run_time / 1000_000_000.0)) + "s");
  }

  private void pass3(List<ParseResult> parseResults) {
    var start_time = nanoTime();
    parseResults.forEach((ps -> {
      new F3(ps.stmts, ps.path).execute();
    }));
    var run_time = (nanoTime() - start_time);
    addInfoLog("pass3 completed in " + ((run_time / 1000_000_000.0)) + "s");
  }

  private void pass4(List<ParseResult> parseResults) {
    var start_time = nanoTime();
    parseResults.forEach((ps -> {
      new F4(ps.stmts, ps.path).execute();
    }));
    var run_time = (nanoTime() - start_time);
    addInfoLog("pass4 completed in " + ((run_time / 1000_000_000.0)) + "s");
  }

  private void pass5(List<ParseResult> parseResults) {
    var start_time = nanoTime();
    parseResults.forEach((ps -> {
      new F5(ps.stmts, ps.path).execute();
    }));
    var run_time = (nanoTime() - start_time);
    addInfoLog("pass5 completed in " + ((run_time / 1000_000_000.0)) + "s");
  }

  private void pass6(List<ParseResult> parseResults) {
    var start_time = nanoTime();
    parseResults.forEach((ps -> {
      addInfoLog("generating code for the constructs defined in " + ps.path);
      new F6(ps.stmts, ps.path).execute();
    }));
    var run_time = (nanoTime() - start_time);
    addInfoLog("code generation for all sources took " + ((run_time / 1000_000_000.0)) + "s");
  }
}
