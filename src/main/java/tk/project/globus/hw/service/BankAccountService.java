package tk.project.globus.hw.service;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.project.globus.hw.dto.account.AccountCreateDto;
import tk.project.globus.hw.dto.account.AccountInfoDto;
import tk.project.globus.hw.dto.account.AccountUpdateDto;
import tk.project.globus.hw.entity.BankAccountEntity;
import tk.project.globus.hw.entity.UserEntity;
import tk.project.globus.hw.exception.BankAccountNotFoundException;
import tk.project.globus.hw.exception.CurrencyNotFoundException;
import tk.project.globus.hw.exception.UserNotFoundException;
import tk.project.globus.hw.mapper.BankAccountMapper;
import tk.project.globus.hw.repository.BankAccountRepository;
import tk.project.globus.hw.repository.CurrencyRepository;
import tk.project.globus.hw.repository.UserRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class BankAccountService {

  private final BankAccountMapper bankAccountMapper;
  private final BankAccountRepository bankAccountRepository;
  private final CurrencyRepository currencyRepository;
  private final CurrencyService currencyService;
  private final UserRepository userRepository;

  public AccountInfoDto create(AccountCreateDto newAccount) {
    throwExceptionIfCurrencyNotExists(newAccount.currencyCharCode());

    UserEntity user = getUserById(newAccount.userId());
    BankAccountEntity accountEntity = bankAccountMapper.toBankAccountEntity(newAccount);
    accountEntity.setUser(user);
    BankAccountEntity savedAccount = bankAccountRepository.save(accountEntity);

    log.debug("Сохранен банковский счет: {}.", savedAccount);
    return bankAccountMapper.toAccountInfoDto(savedAccount);
  }

  @Transactional
  public AccountInfoDto update(AccountUpdateDto accountUpdateDto) {
    BankAccountEntity existingAccount = getAccountById(accountUpdateDto.id());
    BankAccountEntity accountToUpdate =
        updateAccountFields(
            existingAccount, bankAccountMapper.toBankAccountEntity(accountUpdateDto));

    BankAccountEntity updatedAccount = bankAccountRepository.save(accountToUpdate);

    log.debug("Обновлен банковский счет: {}.", updatedAccount);
    return bankAccountMapper.toAccountInfoDto(updatedAccount);
  }

  @Transactional
  public AccountInfoDto changeCurrency(UUID accountId, String currencyCharCode) {
    throwExceptionIfCurrencyNotExists(currencyCharCode);

    BankAccountEntity existingAccount = getAccountById(accountId);
    BankAccountEntity accountToUpdate = changeCurrency(existingAccount, currencyCharCode);
    BankAccountEntity updatedAccount = bankAccountRepository.save(accountToUpdate);

    log.debug("Обновлена валюта банковский счет: {}.", updatedAccount);
    return bankAccountMapper.toAccountInfoDto(updatedAccount);
  }

  public AccountInfoDto getById(UUID accountId, String currencyCharCode) {
    BankAccountEntity account = getAccountById(accountId);

    log.debug("Найден банковский счет: {}.", account);

    if (Objects.nonNull(currencyCharCode)) {
      account = changeCurrency(account, currencyCharCode);
    }
    return bankAccountMapper.toAccountInfoDto(account);
  }

  public List<AccountInfoDto> findAllByUserId(UUID userId, Pageable pageable) {
    List<BankAccountEntity> accounts = bankAccountRepository.findAllByUserId(userId, pageable);
    log.debug("Найден список банковских счетов пользователя с id {}.", userId);
    return bankAccountMapper.toAccountsInfoDto(accounts);
  }

  public AccountInfoDto deleteById(UUID accountId) {
    BankAccountEntity account = getAccountById(accountId);
    bankAccountRepository.delete(account);

    log.debug("Удален банковский счет: {}.", account);
    return bankAccountMapper.toAccountInfoDto(account);
  }

  private UserEntity getUserById(UUID userId) {
    return userRepository
        .findById(userId)
        .orElseThrow(
            () ->
                new UserNotFoundException(
                    String.format("Пользователь с id %s не найден.", userId)));
  }

  private BankAccountEntity getAccountById(UUID accountId) {
    return bankAccountRepository
        .findById(accountId)
        .orElseThrow(
            () ->
                new BankAccountNotFoundException(
                    String.format("Банковский счет с id %s не найден.", accountId)));
  }

  private BankAccountEntity updateAccountFields(
      BankAccountEntity oldAccount, BankAccountEntity newAccount) {

    if (Objects.nonNull(newAccount.getBalance())) {
      oldAccount.setBalance(newAccount.getBalance());
    }
    if (Objects.nonNull(newAccount.getCurrencyCharCode())) {
      throwExceptionIfCurrencyNotExists(newAccount.getCurrencyCharCode());
      oldAccount.setCurrencyCharCode(newAccount.getCurrencyCharCode());
    }
    return oldAccount;
  }

  private void throwExceptionIfCurrencyNotExists(String currencyCharCode) {
    if (currencyRepository.findByCharCode(currencyCharCode).isEmpty()) {
      throw new CurrencyNotFoundException(
          String.format("Валюта c символьным кодом %s не найдена.", currencyCharCode));
    }
  }

  private BankAccountEntity changeCurrency(BankAccountEntity account, String currencyCharCode) {
    return currencyService.changeCurrency(account, currencyCharCode);
  }
}
