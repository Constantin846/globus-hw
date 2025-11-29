package tk.project.globus.hw.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import java.math.BigDecimal;
import java.util.List;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import tk.project.globus.hw.dto.ErrorResponse;
import tk.project.globus.hw.dto.account.AccountCreateDto;
import tk.project.globus.hw.dto.account.AccountInfoDto;
import tk.project.globus.hw.dto.account.AccountUpdateDto;
import tk.project.globus.hw.dto.user.UserInfoDto;
import tk.project.globus.hw.entity.BankAccountEntity;
import tk.project.globus.hw.entity.CurrencyEntity;
import tk.project.globus.hw.entity.UserEntity;
import tk.project.globus.hw.exception.UserNotAccessException;

class BankAccountIntegrationTest extends BaseIntegrationTest {

  @Value("${app.controller.endpoints.accounts}")
  private String accountPath;

  private UserEntity existingUser;
  private CurrencyEntity existingCurrency;
  private BankAccountEntity existingAccount;

  @Test
  @SneakyThrows
  void createAccount() {
    // GIVEN
    saveExistingCurrency();
    saveExistingUser();

    BigDecimal expectedBalance = BigDecimal.valueOf(4587.87);
    AccountCreateDto accountCreateDto =
        new AccountCreateDto(expectedBalance, existingCurrency.getCharCode());

    // WHEN
    String result =
        mockMvc
            .perform(
                post(accountPath)
                    .header(userEmailHeaderKey, existingUser.getEmail())
                    .header(passwordHeaderKey, existingUser.getPassword())
                    .contentType("application/json")
                    .content(objectMapper.writeValueAsString(accountCreateDto)))
            .andDo(print())
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse()
            .getContentAsString();

    AccountInfoDto actualAccount = objectMapper.readValue(result, AccountInfoDto.class);

    // THEN
    assertNotNull(actualAccount.id());
    assertEquals(expectedBalance, actualAccount.balance());
    assertEquals(existingCurrency.getCharCode(), actualAccount.currencyCharCode());
    assertUserEquals(existingUser, actualAccount.user());
  }

  @Test
  @SneakyThrows
  void updateAccount() {
    // GIVEN
    saveExistingAccount();

    BigDecimal expectedBalance = BigDecimal.valueOf(4587.87);
    AccountUpdateDto accountUpdateDto =
        new AccountUpdateDto(existingAccount.getId(), expectedBalance, null);

    // WHEN
    String result =
        mockMvc
            .perform(
                patch(accountPath)
                    .header(userEmailHeaderKey, existingUser.getEmail())
                    .header(passwordHeaderKey, existingUser.getPassword())
                    .contentType("application/json")
                    .content(objectMapper.writeValueAsString(accountUpdateDto)))
            .andDo(print())
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

    AccountInfoDto actualAccount = objectMapper.readValue(result, AccountInfoDto.class);

    // THEN
    assertEquals(existingAccount.getId(), actualAccount.id());
    assertEquals(expectedBalance, actualAccount.balance());
    assertEquals(existingCurrency.getCharCode(), actualAccount.currencyCharCode());
    assertUserEquals(existingUser, actualAccount.user());
  }

  @Test
  @SneakyThrows
  void updateAccountFailedIfUserNotAccess() {
    // GIVEN
    saveExistingAccount();

    BigDecimal expectedBalance = BigDecimal.valueOf(4587.87);
    AccountUpdateDto accountUpdateDto =
        new AccountUpdateDto(existingAccount.getId(), expectedBalance, null);

    UserEntity otherUser = new UserEntity();
    otherUser.setName("other name");
    otherUser.setEmail("other_email@mail");
    otherUser.setPassword("other password");
    userRepository.save(otherUser);

    // WHEN
    String result =
        mockMvc
            .perform(
                patch(accountPath)
                    .header(userEmailHeaderKey, otherUser.getEmail())
                    .header(passwordHeaderKey, otherUser.getPassword())
                    .contentType("application/json")
                    .content(objectMapper.writeValueAsString(accountUpdateDto)))
            .andDo(print())
            .andExpect(status().isForbidden())
            .andReturn()
            .getResponse()
            .getContentAsString();

    ErrorResponse errorResponse = objectMapper.readValue(result, ErrorResponse.class);

    // THEN
    assertEquals(UserNotAccessException.class.getSimpleName(), errorResponse.exceptionName());
  }

  @Test
  @SneakyThrows
  void changeCurrencyOfAccount() {
    // GIVEN
    saveExistingAccount();

    String expectedCurCharCode = "SMF";
    CurrencyEntity otherCurrency = new CurrencyEntity();
    otherCurrency.setCharCode(expectedCurCharCode);
    otherCurrency.setName("name of SMF");
    otherCurrency.setVunitRate(BigDecimal.ONE);
    currencyRepository.save(otherCurrency);

    // WHEN
    String result =
        mockMvc
            .perform(
                patch(accountPath + "/" + existingAccount.getId())
                    .header(userEmailHeaderKey, existingUser.getEmail())
                    .header(passwordHeaderKey, existingUser.getPassword())
                    .contentType("application/json")
                    .param("currency", expectedCurCharCode))
            .andDo(print())
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

    AccountInfoDto actualAccount = objectMapper.readValue(result, AccountInfoDto.class);

    // THEN
    assertEquals(existingAccount.getId(), actualAccount.id());
    assertEquals(expectedCurCharCode, actualAccount.currencyCharCode());
    assertUserEquals(existingUser, actualAccount.user());
  }

  @Test
  @SneakyThrows
  void getAccount() {
    // GIVEN
    saveExistingAccount();

    // WHEN
    String result =
        mockMvc
            .perform(
                get(accountPath + "/" + existingAccount.getId())
                    .header(userEmailHeaderKey, existingUser.getEmail())
                    .header(passwordHeaderKey, existingUser.getPassword())
                    .contentType("application/json"))
            .andDo(print())
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

    AccountInfoDto actualAccount = objectMapper.readValue(result, AccountInfoDto.class);

    // THEN
    assertAccountEquals(existingAccount, existingCurrency, existingUser, actualAccount);
  }

  @Test
  @SneakyThrows
  void findAllAccountsOfUser() {
    // GIVEN
    saveExistingCurrency();
    saveExistingUser();
    BankAccountEntity firstAccount = saveAccount(2342.32, existingUser);
    BankAccountEntity secondAccount = saveAccount(212.32, existingUser);
    BankAccountEntity thirdAccount = saveAccount(2.32, existingUser);

    UserEntity otherUser = new UserEntity();
    otherUser.setName("other name");
    otherUser.setEmail("other_email@mail");
    otherUser.setPassword("other password");
    userRepository.save(otherUser);
    saveAccount(42.3213, otherUser);

    // WHEN
    String result =
        mockMvc
            .perform(
                get(accountPath)
                    .header(userEmailHeaderKey, existingUser.getEmail())
                    .header(passwordHeaderKey, existingUser.getPassword())
                    .contentType("application/json"))
            .andDo(print())
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

    List<AccountInfoDto> actualAccounts =
        objectMapper.readValue(result, new TypeReference<List<AccountInfoDto>>() {});

    // THEN
    assertThat(actualAccounts)
        .anySatisfy(
            actualAccount -> {
              assertAccountEquals(firstAccount, existingCurrency, existingUser, actualAccount);
            })
        .anySatisfy(
            actualAccount -> {
              assertAccountEquals(secondAccount, existingCurrency, existingUser, actualAccount);
            })
        .anySatisfy(
            actualAccount -> {
              assertAccountEquals(thirdAccount, existingCurrency, existingUser, actualAccount);
            })
        .hasSize(3);
  }

  @Test
  @SneakyThrows
  void deleteAccount() {
    // GIVEN
    saveExistingAccount();

    // WHEN
    String result =
        mockMvc
            .perform(
                delete(accountPath + "/" + existingAccount.getId())
                    .header(userEmailHeaderKey, existingUser.getEmail())
                    .header(passwordHeaderKey, existingUser.getPassword())
                    .contentType("application/json"))
            .andDo(print())
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

    AccountInfoDto actualAccount = objectMapper.readValue(result, AccountInfoDto.class);

    // THEN
    assertAccountEquals(existingAccount, existingCurrency, existingUser, actualAccount);
  }

  private void assertAccountEquals(
      BankAccountEntity expectedAccount,
      CurrencyEntity expectedCurrency,
      UserEntity expectedUser,
      AccountInfoDto actualAccount) {

    assertEquals(expectedAccount.getId(), actualAccount.id());
    assertEquals(expectedAccount.getBalance(), actualAccount.balance());
    assertEquals(expectedCurrency.getCharCode(), actualAccount.currencyCharCode());
    assertUserEquals(expectedUser, actualAccount.user());
  }

  private void assertUserEquals(UserEntity expectedUser, UserInfoDto actualUser) {
    assertEquals(expectedUser.getId(), actualUser.id());
    assertEquals(expectedUser.getName(), actualUser.name());
    assertEquals(expectedUser.getEmail(), actualUser.email());
  }

  private void saveExistingUser() {
    existingUser = new UserEntity();
    existingUser.setName("existing name");
    existingUser.setEmail("existing_email@mail");
    existingUser.setPassword("password");
    userRepository.save(existingUser);
  }

  private void saveExistingCurrency() {
    existingCurrency = new CurrencyEntity();
    existingCurrency.setCharCode("EUR");
    existingCurrency.setName("name of EUR");
    existingCurrency.setVunitRate(BigDecimal.valueOf(99.99));
    currencyRepository.save(existingCurrency);
  }

  private void saveExistingAccount() {
    saveExistingUser();
    saveExistingCurrency();
    existingAccount = saveAccount(2342.32, existingUser);
  }

  private BankAccountEntity saveAccount(Double balance, UserEntity user) {
    BankAccountEntity account = new BankAccountEntity();
    account.setBalance(BigDecimal.valueOf(balance));
    account.setCurrencyCharCode(existingCurrency.getCharCode());
    account.setUser(user);
    accountRepository.save(account);
    return account;
  }
}
