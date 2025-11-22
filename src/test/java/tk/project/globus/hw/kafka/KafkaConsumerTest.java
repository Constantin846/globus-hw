package tk.project.globus.hw.kafka;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tk.project.globus.hw.exception.EventHandlerNotFoundException;
import tk.project.globus.hw.kafka.event.Event;
import tk.project.globus.hw.kafka.event.EventSource;
import tk.project.globus.hw.kafka.handler.EventHandler;

@ExtendWith(MockitoExtension.class)
class KafkaConsumerTest {

  @Mock private Map<Event, EventHandler<EventSource>> eventHandlersMock;
  @InjectMocks private KafkaConsumer kafkaConsumerOnTest;

  @Test
  @DisplayName("Fail kafka to listen if eventHandler is not found")
  void listenFailedIfEventHandlerNotFound() {
    // GIVEN
    Event currentEvent = Event.SAVE_CURRENCY;
    EventSource currentEventSource = () -> currentEvent;

    when(eventHandlersMock.containsKey(currentEvent)).thenReturn(false);

    // WHEN // THEN
    assertThrows(
        EventHandlerNotFoundException.class,
        () -> kafkaConsumerOnTest.listen(currentEventSource, () -> {}));
  }
}
