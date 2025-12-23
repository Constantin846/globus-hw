package tk.project.globus.hw.scheduler;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import tk.project.globus.hw.client.CurrencyFeignClient;
import tk.project.globus.hw.config.AppPropertiesConfig;
import tk.project.globus.hw.dto.currency.CurrenciesDto;
import tk.project.globus.hw.entity.CurrencyEntity;
import tk.project.globus.hw.exception.CurrenciesPessimisticLockingException;
import tk.project.globus.hw.mapper.CurrencyMapper;
import tk.project.globus.hw.repository.CurrencyRepository;

@Slf4j
@Component
@ConditionalOnMissingBean(CurrencySchedulerMock.class)
@ConditionalOnProperty(prefix = "app.currency-scheduler", name = "enabled", matchIfMissing = true)
public class CurrencySchedulerImpl implements CurrencyScheduler {

  private final String updateCurrenciesZone;
  private final String dateReqPattern;
  private final CurrencyFeignClient currencyClient;
  private final CurrencyMapper currencyMapper;
  private final CurrencyRepository currencyRepository;

  @Autowired
  public CurrencySchedulerImpl(
      AppPropertiesConfig appPropertiesConfig,
      CurrencyFeignClient currencyClient,
      CurrencyMapper currencyMapper,
      CurrencyRepository currencyRepository) {

    this.updateCurrenciesZone = appPropertiesConfig.getZone();
    this.dateReqPattern =
        appPropertiesConfig.getCurrencyFeignClient().getCurrencies().dateReqPattern();
    this.currencyClient = currencyClient;
    this.currencyMapper = currencyMapper;
    this.currencyRepository = currencyRepository;
  }

  @Override
  @Transactional
  @Scheduled(cron = "${app.currency-scheduler.update-currencies.cron}", zone = "${app.zone}")
  public void updateCurrencies() {
    while (true) {
      try {
        log.info("Запуск шедулера для обновления курсов валют.");

        LocalDate date = LocalDate.ofInstant(Instant.now(), ZoneId.of(updateCurrenciesZone));
        CurrenciesDto result =
            currencyClient.getCurrencies(date.format(DateTimeFormatter.ofPattern(dateReqPattern)));

        List<CurrencyEntity> currenciesToSave =
            currencyMapper.toCurrencyEntities(result.getCurrencies());
        List<String> charCodesToSave =
            currenciesToSave.stream().map(CurrencyEntity::getCharCode).toList();

        Map<String, CurrencyEntity> existingCurrencies =
            currencyRepository.findMapByCharCodeInForUpdateNoWait(charCodesToSave);

        for (CurrencyEntity currency : currenciesToSave) {
          if (existingCurrencies.containsKey(currency.getCharCode())) {
            CurrencyEntity existingCurrency = existingCurrencies.get(currency.getCharCode());
            existingCurrency.setVunitRate(currency.getVunitRate());

          } else {
            currencyRepository.save(currency);
          }
        }
        currencyRepository.saveAll(existingCurrencies.values());

        log.info("Шедулер для обновления курсов валют завершил работу успешно.");
        return;

      } catch (PessimisticLockingFailureException ex) {
        String msgError =
            String.format(
                "Шедулер для обновления курсов валют завершил работу с ошибкой блокировки базы данных: %s.",
                ex.getMessage());
        log.warn(msgError);
        throw new CurrenciesPessimisticLockingException(msgError);

      } catch (Throwable ex) {
        log.warn("Шедулер для обновления курсов валют получил ошибку: {}.", ex.getMessage());
      }
    }
  }
}
