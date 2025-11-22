package tk.project.globus.hw.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tk.project.globus.hw.dto.currency.CurrencySaveDto;
import tk.project.globus.hw.kafka.KafkaProducer;
import tk.project.globus.hw.kafka.event.CurrencySaveEvent;

@Slf4j
@RestController
@RequiredArgsConstructor
@ConditionalOnBean(KafkaProducer.class)
@RequestMapping("${app.controller.base-path}" + "/kafka")
@Tag(name = "KafkaFakeController", description = "API для работы Kafka")
public class KafkaFakeController {

  private final KafkaProducer kafkaProducer;

  @PutMapping("/currencies")
  @Operation(summary = "Сохранение информации о банковской валюте по Kafka")
  public void sendSaveCurrency(@Valid @RequestBody CurrencySaveDto currency) {
    log.info("Получен запрос на сохранение информации по Kafka о банковской валюте {}.", currency);

    kafkaProducer.sendEvent(
        kafkaProducer.getCurrencyTopic(), currency.charCode(), new CurrencySaveEvent(currency));

    log.info(
        "Выполнен запрос на сохранение информации по Kafka о банковской валюте: {}.", currency);
  }
}
