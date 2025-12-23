package tk.project.globus.hw.integration.scheduler;

import static java.util.concurrent.Executors.newSingleThreadExecutor;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import tk.project.globus.hw.exception.CurrenciesPessimisticLockingException;
import tk.project.globus.hw.integration.BaseIntegrationTest;
import tk.project.globus.hw.integration.BaseWireMockTest;
import tk.project.globus.hw.scheduler.CurrencySchedulerImpl;

@Slf4j
class CurrencySchedulerImplTest extends BaseIntegrationTest implements BaseWireMockTest {

  private final ExecutorService executor = newSingleThreadExecutor();

  @Value("${app.zone}")
  private String updateCurrenciesZone;
  @Value("${app.currency-feign-client.get-currencies.date-req-pattern}")
  private String dateReqPattern;
  @Value("${app.currency-feign-client.url}")
  private String currencyFeignClientUrl;

  @Autowired private CurrencySchedulerImpl currencySchedulerImplOnTest;
  @Autowired private CurrencySchedulerAssistantTest currencySchedulerAssistant;

  @DynamicPropertySource
  private static void setWireMockExtension(DynamicPropertyRegistry registry) {
    registry.add("app.currency-feign-client.host", WIRE_MOCK_EXTENSION::baseUrl);
    registry.add("app.currency-scheduler.enabled", () -> true);
  }

  private void addWireMockExtensionStubGetThreeCurrencies() {
    LocalDate date = LocalDate.ofInstant(Instant.now(), ZoneId.of(updateCurrenciesZone));
    String dateReq = date.format(DateTimeFormatter.ofPattern(dateReqPattern));

    addWireMockExtensionStubGet(
        currencyFeignClientUrl + "?date_req=" + dateReq,
        currencySchedulerAssistant.responseBodyOfThreeCurrencies(),
        String.valueOf(MediaType.APPLICATION_XML));
  }

  @Test
  void updateCurrencies() {
    // GIVEN
    addWireMockExtensionStubGetThreeCurrencies();

    // WHEN
    currencySchedulerImplOnTest.updateCurrencies();

    // THEN
    assertEquals(3, currencyRepository.count());
  }

  @Test
  void updateCurrenciesIfCurrencyAlreadyExists() {
    // GIVEN
    addWireMockExtensionStubGetThreeCurrencies();
    currencySchedulerAssistant.saveCurrencyRow();

    // WHEN
    currencySchedulerImplOnTest.updateCurrencies();

    // THEN
    assertEquals(3, currencyRepository.count());
  }

  @Test
  @SneakyThrows
  void updateCurrenciesFailedIfCurrencyRowIsLocked() {
    // GIVEN
    addWireMockExtensionStubGetThreeCurrencies();

    currencySchedulerAssistant.saveCurrencyRow();
    executor.submit(
        () -> {
          try {
            currencySchedulerAssistant.lockCurrencyRow();
            log.info("lockCurrencyRow completed");
          } catch (Exception e) {
            log.warn("lockCurrencyRow threw exception: {}", e.getMessage());
          }
        });

    // WHEN // THEN
    Awaitility.await()
        .atMost(10, TimeUnit.SECONDS)
        .pollInterval(2, TimeUnit.SECONDS)
        .untilAsserted(
            () ->
                assertThrows(
                    CurrenciesPessimisticLockingException.class,
                    () -> currencySchedulerImplOnTest.updateCurrencies()));
  }
}
