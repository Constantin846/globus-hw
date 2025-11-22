package tk.project.globus.hw.kafka;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import tk.project.globus.hw.exception.KafkaSendEventException;
import tk.project.globus.hw.kafka.event.Event;
import tk.project.globus.hw.kafka.event.EventSource;

@ExtendWith(MockitoExtension.class)
class KafkaProducerTest {

  @Mock private KafkaTemplate<String, EventSource> kafkaTemplateEventSourceMock;
  @InjectMocks private KafkaProducer kafkaProducerOnTest;

  @Test
  @DisplayName("Fail to send event if kafkaTemplate fail sending")
  void sendEventFailed() {
    // GIVEN
    String someTopic = "topic";
    String someKey = "key";
    EventSource someEventSource = () -> Event.SAVE_CURRENCY;

    when(kafkaTemplateEventSourceMock.send(someTopic, someKey, someEventSource))
        .thenThrow(new RuntimeException());

    // WHEN // THEN
    assertThrows(
        KafkaSendEventException.class,
        () -> kafkaProducerOnTest.sendEvent(someTopic, someKey, someEventSource));
  }
}
