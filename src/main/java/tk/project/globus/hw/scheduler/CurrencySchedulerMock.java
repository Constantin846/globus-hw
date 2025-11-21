package tk.project.globus.hw.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBooleanProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnBooleanProperty(value = "${app.currency-scheduler.enabled}")
@ConditionalOnExpression("'${app.currency-scheduler.type}'.equals('mock')")
public class CurrencySchedulerMock {

  @Scheduled(
      cron = "${app.currency-scheduler.update-currencies.cron}",
      zone = "${app.currency-scheduler.update-currencies.zone}")
  public void updateCurrencies() throws InterruptedException {
    log.info("Запуск мок шедулера для обновления курсов валют.");

    Thread.sleep(10_000);

    log.info("Мок шедулер для обновления курсов валют завершил работу.");
  }
}
