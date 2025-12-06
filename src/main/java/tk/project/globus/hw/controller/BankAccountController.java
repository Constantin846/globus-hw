package tk.project.globus.hw.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import tk.project.globus.hw.annotation.AuthenticatedUser;
import tk.project.globus.hw.dto.account.AccountCreateDto;
import tk.project.globus.hw.dto.account.AccountInfoDto;
import tk.project.globus.hw.dto.account.AccountUpdateDto;
import tk.project.globus.hw.entity.UserEntity;
import tk.project.globus.hw.service.BankAccountService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("${app.controller.endpoints.accounts}")
@Tag(name = "BankAccountController", description = "API для работы с банковским счетом")
public class BankAccountController {

  private final BankAccountService bankAccountService;

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(summary = "Создание банковского счета")
  public AccountInfoDto create(
      @Valid @RequestBody AccountCreateDto account, @AuthenticatedUser UserEntity authUser) {

    log.info(
        "Получен запрос на создание банковского счета с балансом {} {} и id пользователя {}.",
        account.balance(),
        account.currencyCharCode(),
        authUser.getId());

    AccountInfoDto savedAccount = bankAccountService.create(account, authUser);

    log.info("Выполнен запрос на создание банковского счета: {}", savedAccount);
    return savedAccount;
  }

  @PatchMapping
  @Operation(summary = "Изменение банковского счета")
  public AccountInfoDto update(
      @Valid @RequestBody AccountUpdateDto account, @AuthenticatedUser UserEntity authUser) {

    log.info("Получен запрос на изменение банковского счета: {}.", account);

    AccountInfoDto updatedAccount = bankAccountService.update(account, authUser);

    log.info("Выполнен запрос на изменение банковского счета: {}.", updatedAccount);
    return updatedAccount;
  }

  @PatchMapping("/{accountId}")
  @Operation(summary = "Изменение валюты банковского счета")
  public AccountInfoDto changeCurrency(
      @PathVariable("accountId") UUID accountId,
      @RequestParam("currency") String currency,
      @AuthenticatedUser UserEntity authUser) {

    log.info("Получен запрос на изменение валюты банковского счета c id: {}.", accountId);

    AccountInfoDto updatedAccount =
        bankAccountService.changeCurrency(accountId, currency, authUser);

    log.info("Выполнен запрос на изменение валюты банковского счета: {}.", updatedAccount);
    return updatedAccount;
  }

  @GetMapping("/{accountId}")
  @Operation(summary = "Получение информации о банковском счете")
  public AccountInfoDto getById(
      @PathVariable("accountId") UUID accountId,
      @RequestParam(value = "currency", required = false) String currency,
      @AuthenticatedUser UserEntity authUser) {

    log.info("Получен запрос на получение информации о банковском счете с id {}.", accountId);

    AccountInfoDto foundAccount = bankAccountService.getById(accountId, currency, authUser);

    log.info("Выполнен запрос на получение информации о банковском счете: {}.", foundAccount);
    return foundAccount;
  }

  @GetMapping
  @Operation(summary = "Получение информации о банковских счетах пользователя")
  public List<AccountInfoDto> findAllByUserId(
      @AuthenticatedUser UserEntity authUser,
      @PageableDefault(size = 10, sort = "balance", direction = Sort.Direction.DESC)
          Pageable pageable) {

    log.info(
        "Получен запрос на получение информации о банковских счетах пользователя с id {}.",
        authUser.getId());

    List<AccountInfoDto> accounts = bankAccountService.findAllByUserId(authUser.getId(), pageable);

    log.info(
        "Выполнен запрос на получение информации о банковских счетах пользователя с id {}.",
        authUser.getId());
    return accounts;
  }

  @DeleteMapping("/{accountId}")
  @Operation(summary = "Удаление банковского счета")
  public AccountInfoDto deleteById(
      @PathVariable("accountId") UUID accountId, @AuthenticatedUser UserEntity authUser) {

    log.info("Получен запрос на удаление банковского счета с id {}.", accountId);

    AccountInfoDto deletedAccount = bankAccountService.deleteById(accountId, authUser);

    log.info("Выполнен запрос на удаление банковского счета: {}.", deletedAccount);
    return deletedAccount;
  }
}
