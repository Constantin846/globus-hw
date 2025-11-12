package tk.project.globus.hw.scheduler;

import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import tk.project.globus.hw.client.CurrencyFeignClient;
import tk.project.globus.hw.dto.currency.CurrenciesDto;
import tk.project.globus.hw.entity.CurrencyEntity;
import tk.project.globus.hw.mapper.CurrencyMapper;
import tk.project.globus.hw.repository.CurrencyRepository;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnMissingBean(CurrencySchedulerMock.class)
public class CurrencyScheduler {

  @Value("${app.currency-scheduler.update-currencies.date-req}")
  private String updateCurrenciesDateReq;

  private final CurrencyFeignClient currencyClient;
  private final CurrencyMapper currencyMapper;
  private final CurrencyRepository currencyRepository;

  @PostConstruct
  private void init() {
    if (currencyRepository.count() < 1) {
      updateCurrencies();
    }
  }

  @Transactional
  @Scheduled(
      cron = "${app.currency-scheduler.update-currencies.cron}",
      zone = "${app.currency-scheduler.update-currencies.zone}")
  // @Scheduled(fixedDelay = 30, timeUnit = TimeUnit.SECONDS)
  public void updateCurrencies() {
    log.info("Запуск шедулера для обновления курсов валют.");

    CurrenciesDto result = currencyClient.getCurrencies(updateCurrenciesDateReq);

    List<CurrencyEntity> currenciesToSave =
        currencyMapper.toCurrencyEntities(result.getCurrencies());
    List<String> charCodesToSave =
        currenciesToSave.stream().map(CurrencyEntity::getCharCode).toList();

    Map<String, CurrencyEntity> existingCurrencies =
        currencyRepository.findMapByCharCodeIn(charCodesToSave);

    for (CurrencyEntity currency : currenciesToSave) {
      if (existingCurrencies.containsKey(currency.getCharCode())) {
        CurrencyEntity existingCurrency = existingCurrencies.get(currency.getCharCode());
        existingCurrency.setVunitRate(currency.getVunitRate());

      } else {
        currencyRepository.save(currency);
      }
    }
    currencyRepository.saveAll(existingCurrencies.values());

    log.info("Шедулер для обновления курсов валют завершил работу.");
  }
}
