package de.axonvisualizer.generator;

import de.axonvisualizer.generator.generator.Generator;
import de.axonvisualizer.generator.init.guice.BaseModule;
import de.axonvisualizer.generator.init.guice.StandaloneModule;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class AxonvisualizerApplication {

   //   public static final String INPUT_ROOT = "/Development/qyotta/panozone/";
   public static final String INPUT_ROOT = "/Development/wee/wee-backend/";
   public static final String OUTPUT_PATH = "/Development/axonvisualizer/webapp/src/data/output.json";

   public static void main(String[] args) {
      Injector injector = Guice.createInjector(new BaseModule(INPUT_ROOT, OUTPUT_PATH), new StandaloneModule());

      final Generator generator = injector.getInstance(Generator.class);
      generator.generateFile();
   }
}
