package tk.project.globus.hw.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.project.globus.hw.dto.currency.CurrencyInfoDto;
import tk.project.globus.hw.dto.currency.CurrencySaveDto;
import tk.project.globus.hw.entity.CurrencyChangeable;
import tk.project.globus.hw.entity.CurrencyEntity;
import tk.project.globus.hw.exception.CurrencyNotFoundException;
import tk.project.globus.hw.mapper.CurrencyMapper;
import tk.project.globus.hw.repository.CurrencyRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class CurrencyService {

  private final CurrencyMapper currencyMapper;
  private final CurrencyRepository currencyRepository;

  @Transactional
  public void save(CurrencySaveDto currencySaveDto) {
    Optional<CurrencyEntity> existingCurrencyOp =
        currencyRepository.findByCharCodeForUpdate(currencySaveDto.charCode());

    CurrencyEntity currencyToSave =
        existingCurrencyOp
            .map(
                existingCurrency ->
                    updateNameAndVunitRate(
                        existingCurrency, currencySaveDto.name(), currencySaveDto.vunitRate()))
            .orElseGet(() -> currencyMapper.toCurrencyEntity(currencySaveDto));

    CurrencyEntity savedCurrency = currencyRepository.save(currencyToSave);
    log.debug("Сохранена банковская валюта: {}.", savedCurrency);
  }

  public CurrencyInfoDto getById(UUID currencyId) {
    CurrencyEntity currency = getCurrencyById(currencyId);

    log.debug("Найдена банковская валюта: {}.", currency);
    return currencyMapper.toCurrencyInfoDto(currency);
  }

  public CurrencyInfoDto getByCharCode(String currencyCharCode) {
    CurrencyEntity currency = getCurrencyByCharCode(currencyCharCode);

    log.debug("Найдена банковская валюта по символьному коду: {}.", currency);
    return currencyMapper.toCurrencyInfoDto(currency);
  }

  public List<CurrencyInfoDto> findAll(Pageable pageable) {
    Page<CurrencyEntity> currencies = currencyRepository.findAll(pageable);

    log.debug(
        "Найден список банковских валют с размером {} и номером страницы {}.",
        pageable.getPageSize(),
        pageable.getPageNumber());
    return currencies.stream().map(currencyMapper::toCurrencyInfoDto).toList();
  }

  public <T extends CurrencyChangeable> T changeCurrency(T objToChange, String currencyCharCode) {

    CurrencyEntity oldCurrency = getCurrencyByCharCode(objToChange.getCurrencyCharCode());
    CurrencyEntity newCurrency = getCurrencyByCharCode(currencyCharCode);

    objToChange.setCurrencyCharCode(currencyCharCode);
    objToChange.setBalance(
        objToChange
            .getBalance()
            .multiply(oldCurrency.getVunitRate())
            .divide(newCurrency.getVunitRate(), RoundingMode.HALF_UP));

    log.debug("Баланс пересчитан для другой валюты: {}.", objToChange);
    return objToChange;
  }

  private CurrencyEntity getCurrencyByCharCode(String charCode) {
    return currencyRepository
        .findByCharCode(charCode)
        .orElseThrow(
            () ->
                new CurrencyNotFoundException(
                    String.format(
                        "Банковская валюта c символьным кодом %s не найдена.", charCode)));
  }

  private CurrencyEntity getCurrencyById(UUID currencyId) {
    return currencyRepository
        .findById(currencyId)
        .orElseThrow(
            () ->
                new CurrencyNotFoundException(
                    String.format("Банковская валюта c id %s не найдена.", currencyId)));
  }

  private CurrencyEntity updateNameAndVunitRate(
      CurrencyEntity currency, String name, BigDecimal vunitRate) {

    if (Objects.nonNull(name)) {
      currency.setName(name);
    }
    if (Objects.nonNull(vunitRate)) {
      currency.setVunitRate(vunitRate);
    }
    return currency;
  }
}
