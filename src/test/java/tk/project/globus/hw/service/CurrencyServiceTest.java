package tk.project.globus.hw.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tk.project.globus.hw.entity.BankAccountEntity;
import tk.project.globus.hw.entity.CurrencyChangeable;
import tk.project.globus.hw.entity.CurrencyEntity;
import tk.project.globus.hw.exception.CurrencyNotFoundException;
import tk.project.globus.hw.repository.CurrencyRepository;

@ExtendWith(MockitoExtension.class)
class CurrencyServiceTest {

  @Mock private CurrencyRepository currencyRepositoryMock;
  @InjectMocks private CurrencyService currencyServiceOnTest;

  @Test
  @DisplayName("Change currency successfully")
  void changeCurrency() {
    // GIVEN
    String oldCurCharCode = "RUB";
    String newCurCharCode = "EUR";
    BigDecimal oldVunitRate = BigDecimal.ONE;
    BigDecimal newVunitRate = BigDecimal.TEN;

    BigDecimal oldBalance = BigDecimal.valueOf(30.0);
    BigDecimal newBalance =
        oldBalance.multiply(oldVunitRate).divide(newVunitRate, RoundingMode.HALF_UP);

    CurrencyEntity oldCurrency = new CurrencyEntity();
    oldCurrency.setCharCode(oldCurCharCode);
    oldCurrency.setVunitRate(oldVunitRate);

    CurrencyEntity newCurrency = new CurrencyEntity();
    newCurrency.setCharCode(newCurCharCode);
    newCurrency.setVunitRate(newVunitRate);

    CurrencyChangeable currencyChangeable = new BankAccountEntity();
    currencyChangeable.setCurrencyCharCode(oldCurCharCode);
    currencyChangeable.setBalance(oldBalance);

    when(currencyRepositoryMock.findByCharCode(oldCurCharCode))
        .thenReturn(Optional.of(oldCurrency));
    when(currencyRepositoryMock.findByCharCode(newCurCharCode))
        .thenReturn(Optional.of(newCurrency));

    // WHEN
    CurrencyChangeable actualCurChangeable =
        currencyServiceOnTest.changeCurrency(currencyChangeable, newCurCharCode);

    // THEN
    assertEquals(newCurCharCode, actualCurChangeable.getCurrencyCharCode());
    assertEquals(newBalance, actualCurChangeable.getBalance());
  }

  @Test
  @DisplayName("Fail to change currency if currency is not found")
  void changeCurrencyFailedIfCurrencyNotFound() {
    // GIVEN
    String oldCurCharCode = "RUB";
    String newCurCharCode = "EUR";

    CurrencyEntity oldCurrency = new CurrencyEntity();
    oldCurrency.setCharCode(oldCurCharCode);

    CurrencyEntity newCurrency = new CurrencyEntity();
    newCurrency.setCharCode(newCurCharCode);

    CurrencyChangeable currencyChangeable = new BankAccountEntity();
    currencyChangeable.setCurrencyCharCode(oldCurCharCode);

    when(currencyRepositoryMock.findByCharCode(oldCurCharCode))
        .thenReturn(Optional.of(oldCurrency));
    when(currencyRepositoryMock.findByCharCode(newCurCharCode)).thenReturn(Optional.empty());

    // WHEN // THEN
    assertThrows(
        CurrencyNotFoundException.class,
        () -> currencyServiceOnTest.changeCurrency(currencyChangeable, newCurCharCode));
  }
}
