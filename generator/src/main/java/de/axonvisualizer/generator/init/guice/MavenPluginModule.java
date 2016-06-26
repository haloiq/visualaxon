package de.axonvisualizer.generator.init.guice;

import de.axonvisualizer.generator.json.provider.DataProvider;
import de.axonvisualizer.generator.json.provider.cytoscape.CytoscapeDataProvider;
import de.axonvisualizer.generator.json.writer.JsonWriter;
import de.axonvisualizer.generator.json.writer.gson.GsonWriter;
import de.axonvisualizer.generator.logging.Logger;
import de.axonvisualizer.generator.logging.MavenPluginLogger;

import org.apache.maven.plugin.logging.Log;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

public class MavenPluginModule extends AbstractModule {

   private Log log;

   @Override
   protected void configure() {

      bind(JsonWriter.class).to(GsonWriter.class);
      bind(DataProvider.class).to(CytoscapeDataProvider.class);
      bind(Logger.class).to(MavenPluginLogger.class);
   }

   public MavenPluginModule(final Log log) {
      this.log = log;
   }

   @Provides
   public Log log() {
      return log;
   }
}