package de.visualaxon.generator.init.guice;

import de.visualaxon.generator.logging.Logger;
import de.visualaxon.generator.logging.MavenPluginLogger;

import org.apache.maven.plugin.logging.Log;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

public class MavenPluginModule extends AbstractModule {

   private final Log log;

   @Override
   protected void configure() {

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