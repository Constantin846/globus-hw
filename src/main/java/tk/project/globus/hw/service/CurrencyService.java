package tk.project.globus.hw.service;

import java.math.RoundingMode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tk.project.globus.hw.entity.CurrencyChangeable;
import tk.project.globus.hw.entity.CurrencyEntity;
import tk.project.globus.hw.exception.CurrencyNotFoundException;
import tk.project.globus.hw.repository.CurrencyRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class CurrencyService {

  private final CurrencyRepository currencyRepository;

  public <T extends CurrencyChangeable> T changeCurrency(T objToChange, String currencyCharCode) {

    CurrencyEntity oldCurrency = getByCharCode(objToChange.getCurrencyCharCode());
    CurrencyEntity newCurrency = getByCharCode(currencyCharCode);

    objToChange.setCurrencyCharCode(currencyCharCode);
    objToChange.setBalance(
        objToChange
            .getBalance()
            .multiply(oldCurrency.getVunitRate())
            .divide(newCurrency.getVunitRate(), RoundingMode.HALF_UP));

    log.debug("Баланс пересчитан для другой валюты: {}.", objToChange);
    return objToChange;
  }

  private CurrencyEntity getByCharCode(String charCode) {
    return currencyRepository
        .findByCharCode(charCode)
        .orElseThrow(
            () ->
                new CurrencyNotFoundException(
                    String.format("Валюта c символьным кодом %s не найдена.", charCode)));
  }
}
