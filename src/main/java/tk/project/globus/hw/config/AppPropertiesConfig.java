package tk.project.globus.hw.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "app")
public class AppPropertiesConfig {

  private Controller controller;
  private Kafka kafka;
  private String zone;
  private CurrencyFeignClient currencyFeignClient;

  public record Controller(Endpoints endpoints) {
    public record Endpoints(String users, String accounts, String currencies) {}
  }

  public record Kafka(String currencyTopic) {}

  public record CurrencyFeignClient(GetCurrencies getCurrencies) {
    public record GetCurrencies(String dateReqPattern) {}
  }
}
