package tk.project.globus.hw.kafka;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import tk.project.globus.hw.config.AppPropertiesConfig;
import tk.project.globus.hw.exception.KafkaSendEventException;
import tk.project.globus.hw.kafka.event.Event;
import tk.project.globus.hw.kafka.event.EventSource;

@ExtendWith(MockitoExtension.class)
class KafkaProducerTest {

  private String someTopic;

  @Mock private AppPropertiesConfig appPropertiesConfigMock;
  @Mock private KafkaTemplate<String, EventSource> kafkaTemplateEventSourceMock;
  private KafkaProducer kafkaProducerOnTest;

  @BeforeEach
  void beforeEach() {
    someTopic = "topic";
    when(appPropertiesConfigMock.getKafka()).thenReturn(new AppPropertiesConfig.Kafka(someTopic));
    kafkaProducerOnTest = new KafkaProducer(appPropertiesConfigMock, kafkaTemplateEventSourceMock);
  }

  @Test
  @DisplayName("Fail to send event if kafkaTemplate fail sending")
  void sendEventFailed() {
    // GIVEN
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
