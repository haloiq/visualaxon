package de.axonvisualizer.generator.json.provider.cytoscape;

import de.axonvisualizer.generator.data.Aggregate;
import de.axonvisualizer.generator.data.AxonData;
import de.axonvisualizer.generator.data.CommandHandler;
import de.axonvisualizer.generator.data.EventHandler;
import de.axonvisualizer.generator.data.EventListener;
import de.axonvisualizer.generator.json.provider.DataProvider;
import de.axonvisualizer.generator.json.provider.cytoscape.data.Data;
import de.axonvisualizer.generator.json.provider.cytoscape.data.Node;

import java.util.ArrayList;
import java.util.List;

public class CytoscapeDataProvider implements DataProvider {

   private List<Node> nodes = new ArrayList<>();

   @Override
   public Object getData(final AxonData axonData) {

      final List<Aggregate> aggregates = axonData.getAggregates();

      for (Aggregate aggregate : aggregates) {
         nodes.add(Node.builder()
               .data(Data.builder()
                     .id(aggregate.getName())
                     .name(aggregate.getName())
                     .build())
               .build());

         final List<CommandHandler> commandHandlers = aggregate.getCommandHandlers();

         for (CommandHandler commandHandler : commandHandlers) {
            nodes.add(Node.builder()
                  .data(Data.builder()
                        .id(aggregate.getName() + ":" + commandHandler.getCommand())
                        .name(commandHandler.getCommand())
                        .parent(aggregate.getName())
                        .build())
                  .build());

            for (String event : commandHandler.getEvents()) {
               final List<String> eventHandlerIds = getEventHandlerIds(event, axonData);
               if (eventHandlerIds.isEmpty()) {
                  continue;
               }
               for (String eventHandlerId : eventHandlerIds) {
                  nodes.add(Node.builder()
                        .data(Data.builder()
                              .source(aggregate.getName() + ":" + commandHandler.getCommand())
                              .target(eventHandlerId)
                              .build())
                        .build());
               }
            }
         }
      }

      for (EventListener listener : axonData.getEventListeners()) {
         nodes.add(Node.builder()
               .data(Data.builder()
                     .id(listener.getName())
                     .name(listener.getName())
                     .build())
               .build());

         for (EventHandler eventHandler : listener.getEventHandlers()) {
            nodes.add(Node.builder()
                  .data(Data.builder()
                        .id(listener.getName() + ":" + eventHandler.getEventType())
                        .name(eventHandler.getEventType())
                        .parent(listener.getName())
                        .build())
                  .build());
         }
      }

      return nodes;
   }

   private List<String> getEventHandlerIds(final String event, final AxonData axonData) {
      final List<String> eventHandlerIds = new ArrayList<>();

      for (EventListener eventListener : axonData.getEventListeners()) {
         for (EventHandler eventHandler : eventListener.getEventHandlers()) {
            if (eventHandler.getEventType()
                  .equals(event)) {
               eventHandlerIds.add(eventListener.getName() + ":" + eventHandler.getEventType());
            }
         }
      }

      return eventHandlerIds;
   }
}
