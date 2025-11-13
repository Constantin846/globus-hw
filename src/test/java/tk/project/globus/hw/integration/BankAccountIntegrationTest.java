package tk.project.globus.hw.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import tk.project.globus.hw.dto.account.AccountCreateDto;
import tk.project.globus.hw.dto.account.AccountInfoDto;
import tk.project.globus.hw.dto.account.AccountUpdateDto;
import tk.project.globus.hw.entity.BankAccountEntity;
import tk.project.globus.hw.entity.CurrencyEntity;
import tk.project.globus.hw.entity.UserEntity;

class BankAccountIntegrationTest extends BaseIntegrationTest {

  @Value("${app.controller.endpoints.accounts}")
  private String accountBasePath;

  private UserEntity existingUser;
  private CurrencyEntity existingCurrency;
  private BankAccountEntity existingAccount;

  private void saveExistingUser() {
    existingUser = new UserEntity();
    existingUser.setName("existing name");
    existingUser.setEmail("existing_email@mail");
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
    existingAccount = new BankAccountEntity();
    existingAccount.setBalance(BigDecimal.valueOf(2342.32));
    existingAccount.setCurrencyCharCode(existingCurrency.getCharCode());
    existingAccount.setUser(existingUser);
    accountRepository.save(existingAccount);
  }

  @Test
  @SneakyThrows
  void createAccount() {
    // GIVEN
    saveExistingCurrency();
    saveExistingUser();

    BigDecimal expectedBalance = BigDecimal.valueOf(4587.87);
    AccountCreateDto accountCreateDto =
        new AccountCreateDto(expectedBalance, existingCurrency.getCharCode(), existingUser.getId());

    // WHEN
    String result =
        mockMvc
            .perform(
                post(accountBasePath)
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
    assertEquals(existingUser.getId(), actualAccount.user().id());
    assertEquals(existingUser.getName(), actualAccount.user().name());
    assertEquals(existingUser.getEmail(), actualAccount.user().email());
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
                patch(accountBasePath)
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
    assertEquals(existingUser.getId(), actualAccount.user().id());
    assertEquals(existingUser.getName(), actualAccount.user().name());
    assertEquals(existingUser.getEmail(), actualAccount.user().email());
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
                patch(accountBasePath + "/" + existingAccount.getId())
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
    assertEquals(existingUser.getId(), actualAccount.user().id());
    assertEquals(existingUser.getName(), actualAccount.user().name());
    assertEquals(existingUser.getEmail(), actualAccount.user().email());
  }

  @Test
  @SneakyThrows
  void findAccount() {
    // GIVEN
    saveExistingAccount();

    // WHEN
    String result =
        mockMvc
            .perform(
                get(accountBasePath + "/" + existingAccount.getId())
                    .contentType("application/json"))
            .andDo(print())
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

    AccountInfoDto actualAccount = objectMapper.readValue(result, AccountInfoDto.class);

    // THEN
    assertEquals(existingAccount.getId(), actualAccount.id());
    assertEquals(existingAccount.getBalance(), actualAccount.balance());
    assertEquals(existingCurrency.getCharCode(), actualAccount.currencyCharCode());
    assertEquals(existingUser.getId(), actualAccount.user().id());
    assertEquals(existingUser.getName(), actualAccount.user().name());
    assertEquals(existingUser.getEmail(), actualAccount.user().email());
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
                delete(accountBasePath + "/" + existingAccount.getId())
                    .contentType("application/json"))
            .andDo(print())
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

    AccountInfoDto actualAccount = objectMapper.readValue(result, AccountInfoDto.class);

    // THEN
    assertEquals(existingAccount.getId(), actualAccount.id());
    assertEquals(existingAccount.getBalance(), actualAccount.balance());
    assertEquals(existingCurrency.getCharCode(), actualAccount.currencyCharCode());
    assertEquals(existingUser.getId(), actualAccount.user().id());
    assertEquals(existingUser.getName(), actualAccount.user().name());
    assertEquals(existingUser.getEmail(), actualAccount.user().email());
  }
}
