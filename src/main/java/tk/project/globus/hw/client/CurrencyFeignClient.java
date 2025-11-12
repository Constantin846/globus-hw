package tk.project.globus.hw.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import tk.project.globus.hw.config.CurrencyFeignClientConfig;
import tk.project.globus.hw.dto.currency.CurrenciesDto;

@FeignClient(
    value = "currencyFeignClient",
    url = "${app.currency-feign-client.url}",
    configuration = CurrencyFeignClientConfig.class)
public interface CurrencyFeignClient {

  @GetMapping(produces = "application/json")
  CurrenciesDto getCurrencies(@RequestParam("date_req") String dateReq);
}
