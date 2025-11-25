package tk.project.globus.hw.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import tk.project.globus.hw.dto.currency.CurrenciesDto;

@FeignClient(
    value = "currencyFeignClient",
    url = "${app.currency-feign-client.host}")
public interface CurrencyFeignClient {

  @GetMapping(value = "${app.currency-feign-client.url}", produces = "application/xml")
  CurrenciesDto getCurrencies(@RequestParam("date_req") String dateReq);
}
