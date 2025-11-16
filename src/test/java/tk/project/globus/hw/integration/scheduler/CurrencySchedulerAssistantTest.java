package tk.project.globus.hw.integration.scheduler;

import java.math.BigDecimal;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import tk.project.globus.hw.entity.CurrencyEntity;
import tk.project.globus.hw.repository.CurrencyRepository;

@Slf4j
@Component
class CurrencySchedulerAssistantTest {

  @Autowired private CurrencyRepository currencyRepository;

  private static final String CHAR_CODE_AUD = "AUD";
  private static final String NAME_AUD = "Австралийский доллар";

  @Transactional
  public void saveCurrencyRow() {
    CurrencyEntity currencyAUD = new CurrencyEntity();
    currencyAUD.setName(NAME_AUD);
    currencyAUD.setCharCode(CHAR_CODE_AUD);
    currencyAUD.setVunitRate(BigDecimal.ONE);
    currencyRepository.save(currencyAUD);
  }

  @Transactional
  public void lockCurrencyRow() throws InterruptedException {
    currencyRepository.findAllByCharCodeInForUpdateNoWait(List.of(CHAR_CODE_AUD));
    log.info("start locking currency row");
    Thread.sleep(4_000);
    log.info("finished locking currency row");
  }

  String responseBodyOfThreeCurrencies() {
    return """
        <ValCurs Date="15.11.2025" name="Foreign Currency Market">
          <Valute ID="R01010">
            <NumCode>036</NumCode>
            <CharCode>AUD</CharCode>
            <Nominal>1</Nominal>
            <Name>Австралийский доллар</Name>
            <Value>53,0007</Value>
            <VunitRate>53,0007</VunitRate>
          </Valute>
          <Valute ID="R01020A">
            <NumCode>944</NumCode>
            <CharCode>AZN</CharCode>
            <Nominal>1</Nominal>
            <Name>Азербайджанский манат</Name>
            <Value>47,7221</Value>
            <VunitRate>47,7221</VunitRate>
          </Valute>
          <Valute ID="R01030">
            <NumCode>012</NumCode>
            <CharCode>DZD</CharCode>
            <Nominal>100</Nominal>
            <Name>Алжирских динаров</Name>
            <Value>62,3658</Value>
            <VunitRate>0,623658</VunitRate>
          </Valute>
        </ValCurs>
        """;
  }
}
