package de.axonvisualizer.generator.generator;

import de.axonvisualizer.generator.event.AggregateSpotted;
import de.axonvisualizer.generator.event.CommandHandlerSpotted;
import de.axonvisualizer.generator.event.EventHandlerSpotted;
import de.axonvisualizer.generator.event.EventListenerSpotted;
import de.axonvisualizer.generator.exception.AxonVisualizerException;
import de.axonvisualizer.generator.util.AxonUtil;

import java.util.ArrayList;
import java.util.List;

import org.jboss.forge.roaster.model.Type;
import org.jboss.forge.roaster.model.source.AnnotationSource;
import org.jboss.forge.roaster.model.source.JavaClassSource;
import org.jboss.forge.roaster.model.source.MethodSource;
import org.jboss.forge.roaster.model.source.ParameterSource;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;

public class AxonSpotter {

   private final AxonUtil axonUtil;
   private final EventBus eventBus;

   @Inject
   public AxonSpotter(final EventBus eventBus, final AxonUtil axonUtil) {
      this.eventBus = eventBus;
      this.axonUtil = axonUtil;
   }

   public void getEventListener(final JavaClassSource klass) {

      final List<MethodSource<JavaClassSource>> methods = klass.getMethods();
      final List<MethodSource<JavaClassSource>> eventHandlerMethods = new ArrayList<>();

      for (MethodSource<JavaClassSource> method : methods) {
         final boolean isEventHandlingMethod = method.getAnnotations()
               .stream()
               .anyMatch(javaClassSourceAnnotationSource -> axonUtil.isEventHandlingMethod(javaClassSourceAnnotationSource.getName()));

         if (isEventHandlingMethod) {
            eventHandlerMethods.add(method);
         }
      }

      if (eventHandlerMethods.isEmpty()) {
         return;
      }

      eventBus.post(EventListenerSpotted.builder()
            .name(klass.getName())
            .build());

      for (MethodSource<JavaClassSource> method : eventHandlerMethods) {
         getEventHandler(method, klass.getName());
      }

   }

   public void getEventHandler(final MethodSource<JavaClassSource> method, final String listenerName) {
      final String eventTypeName = method.getParameters()
            .get(0)
            .getType()
            .getName();

      String eventHandlerType = null;

      for (AnnotationSource<JavaClassSource> annotation : method.getAnnotations()) {
         eventHandlerType = axonUtil.getEventHandlerType(annotation.getName());
      }

      if (eventHandlerType == null) {
         throw new AxonVisualizerException(method.getName() + " is not a valid Axon EventHandler");
      }

      eventBus.post(EventHandlerSpotted.builder()
            .eventName(eventTypeName)
            .type(eventHandlerType)
            .listener(listenerName)
            .build());
   }

   public void getAggregate(final JavaClassSource myClass) {

      if (!axonUtil.isAggreagte(myClass)) {
         return;
      }

      final String aggregateName = myClass.getName();

      eventBus.post(AggregateSpotted.builder()
            .name(aggregateName)
            .build());

      final List<MethodSource<JavaClassSource>> methods = myClass.getMethods();

      for (MethodSource<JavaClassSource> method : methods) {
         if (!axonUtil.isCommandHandler(method)) {
            continue;
         }

         final ParameterSource<JavaClassSource> command = method.getParameters()
               .get(0);

         final Type<JavaClassSource> commandType = command.getType();

         final List<String> appliedEvents = axonUtil.getAppliedEvents(method.getBody());

         eventBus.post(CommandHandlerSpotted.builder()
               .command(commandType.getName())
               .aggregate(aggregateName)
               .events(appliedEvents)
               .build());

      }
   }
}
