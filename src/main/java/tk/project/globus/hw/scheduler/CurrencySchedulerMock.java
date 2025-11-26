package tk.project.globus.hw.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnExpression("'${app.currency-scheduler.type}'.equals('mock')")
@ConditionalOnProperty(prefix = "app.currency-scheduler", name = "enabled", matchIfMissing = false)
public class CurrencySchedulerMock implements CurrencyScheduler {

  @Override
  @SneakyThrows
  @Scheduled(cron = "${app.currency-scheduler.update-currencies.cron}", zone = "${app.zone}")
  public void updateCurrencies() {
    log.info("Запуск мок шедулера для обновления курсов валют.");

    Thread.sleep(10_000);

    log.info("Мок шедулер для обновления курсов валют завершил работу.");
  }
}
