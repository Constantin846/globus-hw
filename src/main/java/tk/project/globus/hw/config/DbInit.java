package tk.project.globus.hw.config;

import jakarta.annotation.PostConstruct;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBooleanProperty;
import org.springframework.stereotype.Component;
import tk.project.globus.hw.entity.CurrencyEntity;
import tk.project.globus.hw.repository.CurrencyRepository;
import tk.project.globus.hw.scheduler.CurrencyScheduler;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnBean(CurrencyScheduler.class)
@ConditionalOnBooleanProperty(value = "app.db-init.add-currencies.enabled", matchIfMissing = true)
public class DbInit {

  private final CurrencyRepository currencyRepository;
  private final CurrencyScheduler currencyScheduler;

  @PostConstruct
  private void init() {
    log.info("Добавление информации о валютах в базу данных.");

    if (currencyRepository.findByCharCode(CHAR_CODE_RUB).isEmpty()) {
      currencyRepository.save(buildCurrencyRUB());
    }
    currencyScheduler.updateCurrencies();

    log.info("Добавление информации о валютах в базу данных завершено.");
  }

  private static final String CHAR_CODE_RUB = "RUB";
  private static final String NAME_OF_RUB = "Рубль";

  private CurrencyEntity buildCurrencyRUB() {
    CurrencyEntity currencyRUB = new CurrencyEntity();
    currencyRUB.setCharCode(CHAR_CODE_RUB);
    currencyRUB.setName(NAME_OF_RUB);
    currencyRUB.setVunitRate(BigDecimal.ONE);
    return currencyRUB;
  }
}
