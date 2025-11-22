package tk.project.globus.hw.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tk.project.globus.hw.dto.currency.CurrencyInfoDto;
import tk.project.globus.hw.service.CurrencyService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("${app.controller.endpoints.currencies}")
@Tag(name = "CurrencyController", description = "API для работы с банковскими валютами")
public class CurrencyController {

  private final CurrencyService currencyService;

  @GetMapping("/{currencyId}")
  @Operation(summary = "Получение информации о банковской валюте")
  public CurrencyInfoDto getById(@PathVariable("currencyId") UUID currencyId) {
    log.info("Получен запрос на получение информации о банковской валюте с id {}.", currencyId);

    CurrencyInfoDto foundCurrency = currencyService.getById(currencyId);

    log.info("Выполнен запрос на получение информации о банковской валюте: {}.", foundCurrency);
    return foundCurrency;
  }

  @GetMapping("/char-code/{currencyCharCode}")
  @Operation(summary = "Получение информации о банковской валюте по символьному коду")
  public CurrencyInfoDto getByCharCode(@PathVariable("currencyCharCode") String currencyCharCode) {
    log.info(
        "Получен запрос на получение информации о банковской валюте с символьным кодом {}.",
        currencyCharCode);

    CurrencyInfoDto foundCurrency = currencyService.getByCharCode(currencyCharCode);

    log.info(
        "Выполнен запрос на получение информации о банковской валюте по символьному коду: {}.",
        foundCurrency);
    return foundCurrency;
  }

  @GetMapping
  @Operation(summary = "Получение информации о банковских валютах")
  public List<CurrencyInfoDto> findAll(
      @PageableDefault(size = 30, sort = "charCode", direction = Sort.Direction.ASC)
          Pageable pageable) {

    log.info(
        "Получен запрос на получение информации о банковских валютах с размером {} и номером страницы {}.",
        pageable.getPageSize(),
        pageable.getPageNumber());

    List<CurrencyInfoDto> currencies = currencyService.findAll(pageable);

    log.info(
        "Выполнен запрос на получение информации о банковских валютах с размером {} и номером страницы {}.",
        pageable.getPageSize(),
        pageable.getPageNumber());
    return currencies;
  }
}
