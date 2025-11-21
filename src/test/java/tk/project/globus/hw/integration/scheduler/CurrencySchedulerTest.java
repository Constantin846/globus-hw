package tk.project.globus.hw.integration.scheduler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import tk.project.globus.hw.exception.CurrenciesPessimisticLockingException;
import tk.project.globus.hw.integration.BaseIntegrationTest;
import tk.project.globus.hw.integration.BaseWireMockTest;
import tk.project.globus.hw.scheduler.CurrencyScheduler;

@Slf4j
class CurrencySchedulerTest extends BaseIntegrationTest implements BaseWireMockTest {

  @Value("${app.zone}")
  private String updateCurrenciesZone;

  @Value("${app.currency-feign-client.get-currencies.date-req.pattern}")
  private String dateReqPattern;

  @Value("${app.currency-feign-client.url}")
  private String currencyFeignClientUrl;

  @Autowired private CurrencyScheduler currencyScheduler;
  @Autowired private CurrencySchedulerAssistantTest currencySchedulerAssistant;

  @RegisterExtension
  private static final WireMockExtension wireMockExtension =
      WireMockExtension.newInstance()
          .options(WireMockConfiguration.wireMockConfig().dynamicPort().dynamicPort())
          .build();

  @DynamicPropertySource
  private static void setWireMockExtension(DynamicPropertyRegistry registry) {
    registry.add("app.currency-feign-client.host", wireMockExtension::baseUrl);
  }

  @Override
  public WireMockExtension getWireMockExtension() {
    return wireMockExtension;
  }

  private void addWireMockExtensionStubGet() {
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
    addWireMockExtensionStubGet();

    // WHEN
    currencyScheduler.updateCurrencies();

    // THEN
    assertEquals(3, currencyRepository.count());
  }

  @Test
  void updateCurrenciesIfCurrencyAlreadyExists() {
    // GIVEN
    addWireMockExtensionStubGet();
    currencySchedulerAssistant.saveCurrencyRow();

    // WHEN
    currencyScheduler.updateCurrencies();

    // THEN
    assertEquals(3, currencyRepository.count());
  }

  @Test
  @SneakyThrows
  void updateCurrenciesFailedIfCurrencyRowIsLocked() {
    // GIVEN
    addWireMockExtensionStubGet();

    currencySchedulerAssistant.saveCurrencyRow();
    new Thread(
            () -> {
              try {
                currencySchedulerAssistant.lockCurrencyRow();
                log.info("lockCurrencyRow completed");
              } catch (Exception e) {
                log.warn("lockCurrencyRow threw exception: {}", e.getMessage());
              }
            })
        .start();

    // WHEN // THEN
    Awaitility.await()
        .atMost(10, TimeUnit.SECONDS)
        .pollInterval(2, TimeUnit.SECONDS)
        .untilAsserted(
            () ->
                assertThrows(
                    CurrenciesPessimisticLockingException.class,
                    () -> currencyScheduler.updateCurrencies()));
  }
}
