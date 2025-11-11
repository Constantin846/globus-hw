package tk.project.globus.hw.entity;

import java.math.BigDecimal;

public interface CurrencyChangeable {

  void setBalance(BigDecimal balance);

  BigDecimal getBalance();

  void setCurrencyCharCode(String currencyCharCode);

  String getCurrencyCharCode();
}
