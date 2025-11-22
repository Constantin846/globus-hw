package tk.project.globus.hw.integration.kafka;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import lombok.SneakyThrows;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import tk.project.globus.hw.dto.currency.CurrencySaveDto;
import tk.project.globus.hw.entity.CurrencyEntity;
import tk.project.globus.hw.integration.BaseIntegrationTest;
import tk.project.globus.hw.kafka.KafkaProducer;
import tk.project.globus.hw.kafka.event.CurrencySaveEvent;

class KafkaIntegrationTest extends BaseIntegrationTest implements KafkaTestContainer {

  @Autowired KafkaProducer kafkaProducer;

  private CurrencyEntity existingCurrency;

  @Test
  @SneakyThrows
  void saveCurrency() {
    // GIVEN
    String expectedCharCode = "SMF";
    String expectedName = "currency";
    BigDecimal expectedVunitRate = BigDecimal.valueOf(123.12);

    CurrencySaveDto currencySaveDto =
        new CurrencySaveDto(expectedCharCode, expectedName, expectedVunitRate);

    // WHEN
    kafkaProducer.sendEvent(
        kafkaProducer.getCurrencyTopic(), expectedCharCode, new CurrencySaveEvent(currencySaveDto));

    // THEN
    Awaitility.await()
        .atMost(10, TimeUnit.SECONDS)
        .pollInterval(2, TimeUnit.SECONDS)
        .untilAsserted(
            () -> {
              Optional<CurrencyEntity> actualCurrencyOp =
                  currencyRepository.findByCharCode(expectedCharCode);
              assertTrue(actualCurrencyOp.isPresent());

              CurrencyEntity actualCurrency = actualCurrencyOp.get();
              assertCurrencyEquals(
                  expectedCharCode, expectedName, expectedVunitRate, actualCurrency);
            });
  }

  @Test
  @SneakyThrows
  void saveCurrencyIfCurrencyAlreadyExists() {
    // GIVEN
    saveExistingCurrency();
    String expectedCharCode = existingCurrency.getCharCode();
    String expectedName = "currency";
    BigDecimal expectedVunitRate = BigDecimal.valueOf(123.12);

    CurrencySaveDto currencySaveDto =
        new CurrencySaveDto(expectedCharCode, expectedName, expectedVunitRate);

    // WHEN
    kafkaProducer.sendEvent(
        kafkaProducer.getCurrencyTopic(), expectedCharCode, new CurrencySaveEvent(currencySaveDto));

    // THEN
    Awaitility.await()
        .atMost(10, TimeUnit.SECONDS)
        .pollInterval(2, TimeUnit.SECONDS)
        .untilAsserted(
            () -> {
              Optional<CurrencyEntity> actualCurrencyOp =
                  currencyRepository.findByCharCode(expectedCharCode);
              assertTrue(actualCurrencyOp.isPresent());

              CurrencyEntity actualCurrency = actualCurrencyOp.get();
              assertCurrencyEquals(
                  expectedCharCode, expectedName, expectedVunitRate, actualCurrency);
            });
  }

  @Test
  @SneakyThrows
  void notSaveCurrencyFieldsToNullIfCurrencyAlreadyExists() {
    // GIVEN
    saveExistingCurrency();
    String expectedCharCode = existingCurrency.getCharCode();
    CurrencySaveDto currencySaveDto = new CurrencySaveDto(expectedCharCode, null, null);

    // WHEN
    kafkaProducer.sendEvent(
        kafkaProducer.getCurrencyTopic(), expectedCharCode, new CurrencySaveEvent(currencySaveDto));

    // THEN
    Awaitility.await()
        .atMost(10, TimeUnit.SECONDS)
        .pollInterval(2, TimeUnit.SECONDS)
        .untilAsserted(
            () -> {
              Optional<CurrencyEntity> actualCurrencyOp =
                  currencyRepository.findByCharCode(expectedCharCode);
              assertTrue(actualCurrencyOp.isPresent());

              CurrencyEntity actualCurrency = actualCurrencyOp.get();
              assertCurrencyEquals(
                  expectedCharCode,
                  existingCurrency.getName(),
                  existingCurrency.getVunitRate(),
                  actualCurrency);
            });
  }

  private void assertCurrencyEquals(
      String expectedCharCode,
      String expectedName,
      BigDecimal expectedVunitRate,
      CurrencyEntity actualCurrency) {

    assertEquals(expectedCharCode, actualCurrency.getCharCode());
    assertEquals(expectedName, actualCurrency.getName());
    assertEquals(
        expectedVunitRate.setScale(2, RoundingMode.HALF_UP),
        actualCurrency.getVunitRate().setScale(2, RoundingMode.HALF_UP));
  }

  private void saveExistingCurrency() {
    existingCurrency = new CurrencyEntity();
    existingCurrency.setCharCode("CHR");
    existingCurrency.setName("name of CHR");
    existingCurrency.setVunitRate(BigDecimal.valueOf(99.23));
    currencyRepository.save(existingCurrency);
  }
}
