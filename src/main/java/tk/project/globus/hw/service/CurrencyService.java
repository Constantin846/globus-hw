package tk.project.globus.hw.service;

import java.math.RoundingMode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tk.project.globus.hw.entity.BankAccountEntity;
import tk.project.globus.hw.entity.CurrencyEntity;
import tk.project.globus.hw.exception.CurrencyNotFoundException;
import tk.project.globus.hw.repository.CurrencyRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class CurrencyService {

  private final CurrencyRepository currencyRepository;

  public BankAccountEntity changeCurrency(BankAccountEntity account, String currencyCharCode) {

    CurrencyEntity oldCurrency = getByCharCode(account.getCurrencyCharCode());
    CurrencyEntity newCurrency = getByCharCode(currencyCharCode);

    account.setCurrencyCharCode(currencyCharCode);
    account.setBalance(
        account
            .getBalance()
            .multiply(oldCurrency.getVunitRate())
            .divide(newCurrency.getVunitRate(), RoundingMode.HALF_UP));

    log.debug("Баланс банковского счет пересчитан для другой валюты: {}.", account);
    return account;
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
