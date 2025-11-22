package tk.project.globus.hw.config;

import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.util.backoff.FixedBackOff;
import tk.project.globus.hw.kafka.event.EventSource;

@Slf4j
@EnableKafka
@Configuration
@ConditionalOnProperty(prefix = "app.kafka", name = "enabled", matchIfMissing = false)
public class KafkaConsumerConfig {

  @Value("${app.kafka.bootstrap-address}")
  private String bootstrapAddress;

  @Value("${app.kafka.group-id}")
  private String groupId;

  @Value("${app.kafka.session-timeout.ms:15000}")
  private String sessionTimeout;

  private ConsumerFactory<String, EventSource> consumerFactoryEventSource() {
    Map<String, Object> props = new HashMap<>();
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
    props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
    props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, "tk.project.globus.hw.kafka.event.EventSource");
    props.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, false);
    props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, sessionTimeout);
    props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
    return new DefaultKafkaConsumerFactory<>(props);
  }

  @Bean
  public ConcurrentKafkaListenerContainerFactory<String, EventSource>
      kafkaListenerContainerFactoryEventSource() {

    ConcurrentKafkaListenerContainerFactory<String, EventSource> factory =
        new ConcurrentKafkaListenerContainerFactory<>();

    factory.setConsumerFactory(consumerFactoryEventSource());
    factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
    factory.setCommonErrorHandler(errorHandler());
    return factory;
  }

  private CommonErrorHandler errorHandler() {
    DefaultErrorHandler errorHandler = new DefaultErrorHandler(new FixedBackOff(10_000, 3));
    errorHandler.addNotRetryableExceptions(IllegalArgumentException.class);
    errorHandler.setRetryListeners(
        (record, ex, deliveryAttempt) -> {
          log.warn(
              "RetryListeners message = {}, offset = {}, deliveryAttempt = {}",
              ex.getMessage(),
              record.offset(),
              deliveryAttempt);
        });
    return errorHandler;
  }
}
