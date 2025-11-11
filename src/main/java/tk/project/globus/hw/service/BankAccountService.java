package tk.project.globus.hw.service;

import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.project.globus.hw.dto.account.AccountCreateDto;
import tk.project.globus.hw.dto.account.AccountInfoDto;
import tk.project.globus.hw.dto.account.AccountUpdateDto;
import tk.project.globus.hw.entity.BankAccountEntity;
import tk.project.globus.hw.entity.UserEntity;
import tk.project.globus.hw.exception.BankAccountNotFoundException;
import tk.project.globus.hw.exception.UserNotFoundException;
import tk.project.globus.hw.mapper.BankAccountMapper;
import tk.project.globus.hw.repository.BankAccountRepository;
import tk.project.globus.hw.repository.UserRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class BankAccountService {

  private final BankAccountMapper bankAccountMapper;
  private final BankAccountRepository bankAccountRepository;
  private final UserRepository userRepository;

  @Transactional
  public AccountInfoDto create(AccountCreateDto newAccount) {
    UserEntity user = getUserById(newAccount.user_id());
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

    bankAccountRepository.save(accountToUpdate);
    BankAccountEntity updatedAccount = getAccountById(accountToUpdate.getId());

    log.debug("Обновлен банковский счет: {}.", updatedAccount);
    return bankAccountMapper.toAccountInfoDto(updatedAccount);
  }

  public AccountInfoDto getById(UUID accountId, String currency) {
    BankAccountEntity account = getAccountById(accountId);

    log.debug("Найден банковский счет: {}.", account);

    if (Objects.nonNull(currency)) {
      changeCurrency(account, currency);
    }
    return bankAccountMapper.toAccountInfoDto(account);
  }

  @Transactional
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
                    String.format("Пользователь с id %s не найдено.", userId)));
  }

  private BankAccountEntity getAccountById(UUID accountId) {
    return bankAccountRepository
        .findById(accountId)
        .orElseThrow(
            () ->
                new BankAccountNotFoundException(
                    String.format("Банковский счет с id %s не найдено.", accountId)));
  }

  private BankAccountEntity updateAccountFields(
      BankAccountEntity oldAccount, BankAccountEntity newAccount) {

    if (Objects.nonNull(newAccount.getBalance())) {
      oldAccount.setBalance(newAccount.getBalance());
    }
    if (Objects.nonNull(newAccount.getCurrency())) {
      oldAccount.setCurrency(newAccount.getCurrency());
    }
    return oldAccount;
  }

  private BankAccountEntity changeCurrency(BankAccountEntity account, String currency) {
    // todo
    return account;
  }
}
