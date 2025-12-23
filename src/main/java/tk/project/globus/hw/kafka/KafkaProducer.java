package tk.project.globus.hw.kafka;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import tk.project.globus.hw.config.AppPropertiesConfig;
import tk.project.globus.hw.exception.KafkaSendEventException;
import tk.project.globus.hw.kafka.event.EventSource;

@Slf4j
@Service
@ConditionalOnProperty(prefix = "app.kafka", name = "enabled", matchIfMissing = false)
public class KafkaProducer {

  @Getter private final String currencyTopic;
  private final KafkaTemplate<String, EventSource> kafkaTemplateEventSource;

  @Autowired
  public KafkaProducer(
      AppPropertiesConfig appPropertiesConfig,
      KafkaTemplate<String, EventSource> kafkaTemplateEventSource) {

    this.currencyTopic = appPropertiesConfig.getKafka().currencyTopic();
    this.kafkaTemplateEventSource = kafkaTemplateEventSource;
  }

  public void sendEvent(String topic, String key, EventSource event) {
    Assert.hasText(topic, "Топик не должен быть пустым");
    Assert.hasText(key, "Ключ не должен быть пустым");
    Assert.notNull(event, "Событие на должно быть null");

    try {
      kafkaTemplateEventSource.send(topic, key, event).join();
      log.info("Сообщение отправлено по Kafka успешно: {}.", event);

    } catch (Exception e) {
      String msg = String.format("Сообщение отправлено по Kafka с ошибкой: %s.", e.getMessage());
      log.warn(msg);
      throw new KafkaSendEventException(msg);
    }
  }
}
