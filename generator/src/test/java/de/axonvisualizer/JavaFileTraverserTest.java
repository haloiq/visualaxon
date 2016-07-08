package de.axonvisualizer;

import de.axonvisualizer.config.AbstractTest;
import de.axonvisualizer.generator.generator.JavaFileTraverser;

import static org.junit.Assert.assertTrue;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import com.google.inject.Inject;

public class JavaFileTraverserTest extends AbstractTest {

   @Inject
   private JavaFileTraverser javaFileTraverser;

   @Test
   public void shouldOnlyTraverseJavaFiles() throws Exception {
      javaFileTraverser.traverse(path -> assertTrue(path.toAbsolutePath()
            .toString()
            .endsWith(".java")));
   }

   @Test
   public void shouldNotVisitFileTwice() throws Exception {
      Set<Path> seenFiles = new HashSet<>();
      javaFileTraverser.traverse(new JavaFileTraverser.TraverseCallback() {
         @Override
         public void onFile(final Path path) {
            assertTrue(path.getFileName()
                  .toString() + " was already vistited", seenFiles.add(path.toAbsolutePath()));
         }
      });
   }
}
