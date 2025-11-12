package tk.project.globus.hw.config;

import jakarta.annotation.PostConstruct;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBooleanProperty;
import org.springframework.stereotype.Component;
import tk.project.globus.hw.client.CurrencyFeignClient;
import tk.project.globus.hw.dto.currency.CurrenciesDto;
import tk.project.globus.hw.entity.CurrencyEntity;
import tk.project.globus.hw.mapper.CurrencyMapper;
import tk.project.globus.hw.repository.CurrencyRepository;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnBooleanProperty(value = "app.db-init.add-currencies.enabled", matchIfMissing = true)
public class DbInit {

  @Value("${app.zone}")
  private String updateCurrenciesZone;
  @Value("${app.currency-feign-client.get-currencies.date-req.pattern}")
  private String dateReqPattern;

  private final CurrencyFeignClient currencyClient;
  private final CurrencyMapper currencyMapper;
  private final CurrencyRepository currencyRepository;

  @PostConstruct
  private void init() {
    if (currencyRepository.count() < 1) {
      log.info("Добавление информации о валютах в базу данных");

      currencyRepository.save(buildRUB());

      LocalDate date = LocalDate.ofInstant(Instant.now(), ZoneId.of(updateCurrenciesZone));
      CurrenciesDto result =
          currencyClient.getCurrencies(date.format(DateTimeFormatter.ofPattern(dateReqPattern)));

      List<CurrencyEntity> currenciesToSave =
          currencyMapper.toCurrencyEntities(result.getCurrencies());
      currencyRepository.saveAll(currenciesToSave);

      log.info("Добавление информации о валютах в базу данных завершено");
    }
  }

  private static final String CHAR_CODE_RUB = "RUB";
  private static final String NAME_OF_RUB = "Рубль";

  private CurrencyEntity buildRUB() {
    CurrencyEntity currencyRUB = new CurrencyEntity();
    currencyRUB.setCharCode(CHAR_CODE_RUB);
    currencyRUB.setName(NAME_OF_RUB);
    currencyRUB.setVunitRate(BigDecimal.ONE);
    return currencyRUB;
  }
}
