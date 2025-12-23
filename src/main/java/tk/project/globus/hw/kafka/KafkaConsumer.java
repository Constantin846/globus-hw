package tk.project.globus.hw.kafka;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;
import tk.project.globus.hw.exception.EventHandlerNotFoundException;
import tk.project.globus.hw.kafka.event.Event;
import tk.project.globus.hw.kafka.event.EventSource;
import tk.project.globus.hw.kafka.handler.EventHandler;

@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "app.kafka", name = "enabled", matchIfMissing = false)
public class KafkaConsumer {

  private final Map<Event, EventHandler<EventSource>> eventHandlers;

  @KafkaListener(
      topics = "${app.kafka.currency-topic}",
      containerFactory = "kafkaListenerContainerFactoryEventSource")
  public void listen(EventSource eventSource, Acknowledgment acknowledgment) {
    log.info("Получено сообщение: {}.", eventSource);

    if (!eventHandlers.containsKey(eventSource.getEvent())) {
      acknowledgment.acknowledge();
      String msg = String.format("Не найден обработчик для события: %s.", eventSource.getEvent());
      log.warn(msg);
      throw new EventHandlerNotFoundException(msg, eventSource.getEvent().name());
    }

    eventHandlers.get(eventSource.getEvent()).handleEvent(eventSource);
    acknowledgment.acknowledge();

    log.info("Сообщение успешно обработано: {}.", eventSource);
  }
}
