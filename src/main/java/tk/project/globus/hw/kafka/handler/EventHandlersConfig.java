package tk.project.globus.hw.kafka.handler;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tk.project.globus.hw.kafka.event.Event;
import tk.project.globus.hw.kafka.event.EventSource;

@Configuration
public class EventHandlersConfig {

  @Bean
  public <T extends EventSource> Map<Event, EventHandler<T>> eventHandlers(
      Set<EventHandler<T>> eventHandlers) {

    return eventHandlers.stream()
        .collect(Collectors.toMap(EventHandler::getEvent, Function.identity()));
  }
}
