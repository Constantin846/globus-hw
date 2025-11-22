package tk.project.globus.hw.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import tk.project.globus.hw.dto.account.AccountCreateDto;
import tk.project.globus.hw.dto.account.AccountInfoDto;
import tk.project.globus.hw.dto.account.AccountUpdateDto;
import tk.project.globus.hw.entity.BankAccountEntity;
import tk.project.globus.hw.entity.CurrencyEntity;
import tk.project.globus.hw.entity.UserEntity;
import tk.project.globus.hw.exception.BankAccountNotFoundException;
import tk.project.globus.hw.exception.CurrencyNotFoundException;
import tk.project.globus.hw.mapper.BankAccountMapper;
import tk.project.globus.hw.repository.BankAccountRepository;
import tk.project.globus.hw.repository.CurrencyRepository;
import tk.project.globus.hw.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class BankAccountServiceTest {

  @Spy private BankAccountMapper accountMapper = BankAccountMapper.MAPPER;
  @Mock private BankAccountRepository accountRepositoryMock;
  @Mock private CurrencyRepository currencyRepositoryMock;
  @Mock private CurrencyService currencyServiceMock;
  @Mock private UserRepository userRepositoryMock;
  @InjectMocks private BankAccountService accountServiceOnTest;

  @Test
  @DisplayName("Create bank account successfully")
  void createAccount() {
    // GIVEN
    UUID existingUserId = UUID.randomUUID();
    UserEntity existingUser = new UserEntity();
    existingUser.setId(existingUserId);
    existingUser.setEmail("email@email");
    existingUser.setName("user name");

    UUID expectedAccountId = UUID.randomUUID();
    BigDecimal expectedBalance = BigDecimal.valueOf(33.33);
    String expectedCurCharCode = "USD";

    AccountCreateDto accountCreateDto = new AccountCreateDto(expectedBalance, expectedCurCharCode);

    BankAccountEntity accountToSave = accountMapper.toBankAccountEntity(accountCreateDto);
    accountToSave.setUser(existingUser);

    BankAccountEntity savedAccount = new BankAccountEntity();
    savedAccount.setId(expectedAccountId);
    savedAccount.setBalance(expectedBalance);
    savedAccount.setCurrencyCharCode(expectedCurCharCode);
    savedAccount.setUser(existingUser);

    AccountInfoDto expectedAccount = accountMapper.toAccountInfoDto(savedAccount);

    when(currencyRepositoryMock.findByCharCode(expectedCurCharCode))
        .thenReturn(Optional.of(new CurrencyEntity()));
    when(accountRepositoryMock.save(accountToSave)).thenReturn(savedAccount);

    // WHEN
    AccountInfoDto actualAccount = accountServiceOnTest.create(accountCreateDto, existingUser);

    // THEN
    assertEquals(expectedAccount, actualAccount);
  }

  @Test
  @DisplayName("Fail to create bank account if currency does not exist")
  void createAccountFailedIfCurrencyNotExists() {
    // GIVEN
    BigDecimal anyBalance = BigDecimal.valueOf(33.33);
    String notExistingCurCharCode = "EUR";

    AccountCreateDto accountCreateDto = new AccountCreateDto(anyBalance, notExistingCurCharCode);

    when(currencyRepositoryMock.findByCharCode(notExistingCurCharCode))
        .thenReturn(Optional.empty());

    // WHEN // THEN
    assertThrows(
        CurrencyNotFoundException.class,
        () -> accountServiceOnTest.create(accountCreateDto, new UserEntity()));
  }

  @Test
  @DisplayName("Update bank account successfully")
  void updateAccount() {
    // GIVEN
    UserEntity existingUser = new UserEntity();
    existingUser.setId(UUID.randomUUID());

    UUID existingAccountId = UUID.randomUUID();
    BankAccountEntity existingAccount = new BankAccountEntity();
    existingAccount.setId(existingAccountId);
    existingAccount.setUser(existingUser);

    BigDecimal expectedBalance = BigDecimal.valueOf(33.33);
    String expectedCurCharCode = "EUR";

    AccountUpdateDto accountUpdateDto =
        new AccountUpdateDto(existingAccountId, expectedBalance, expectedCurCharCode);

    BankAccountEntity accountToSave = accountMapper.toBankAccountEntity(accountUpdateDto);
    accountToSave.setUser(existingUser);

    BankAccountEntity savedAccount = new BankAccountEntity();
    savedAccount.setId(existingAccountId);
    savedAccount.setBalance(expectedBalance);
    savedAccount.setCurrencyCharCode(expectedCurCharCode);
    savedAccount.setUser(existingUser);

    AccountInfoDto expectedAccount = accountMapper.toAccountInfoDto(savedAccount);

    when(accountRepositoryMock.findById(existingAccountId))
        .thenReturn(Optional.of(existingAccount));
    when(currencyRepositoryMock.findByCharCode(expectedCurCharCode))
        .thenReturn(Optional.of(new CurrencyEntity()));
    when(accountRepositoryMock.save(accountToSave)).thenReturn(savedAccount);

    // WHEN
    AccountInfoDto actualAccount = accountServiceOnTest.update(accountUpdateDto, existingUser);

    // THEN
    assertEquals(expectedAccount, actualAccount);
  }

  @Test
  @DisplayName("Not update account fields to null")
  void notUpdateAccountFieldsToNull() {
    // GIVEN
    UserEntity existingUser = new UserEntity();
    existingUser.setId(UUID.randomUUID());

    UUID existingAccountId = UUID.randomUUID();
    BigDecimal expectedBalance = BigDecimal.valueOf(33.33);
    String expectedCurCharCode = "EUR";

    BankAccountEntity existingAccount = new BankAccountEntity();
    existingAccount.setId(existingAccountId);
    existingAccount.setBalance(expectedBalance);
    existingAccount.setCurrencyCharCode(expectedCurCharCode);
    existingAccount.setUser(existingUser);

    AccountUpdateDto accountUpdateDto = new AccountUpdateDto(existingAccountId, null, null);

    BankAccountEntity savedAccount = new BankAccountEntity();
    savedAccount.setId(existingAccountId);
    savedAccount.setBalance(expectedBalance);
    savedAccount.setCurrencyCharCode(expectedCurCharCode);
    savedAccount.setUser(existingUser);

    AccountInfoDto expectedAccount = accountMapper.toAccountInfoDto(savedAccount);

    when(accountRepositoryMock.findById(existingAccountId))
        .thenReturn(Optional.of(existingAccount));
    when(accountRepositoryMock.save(existingAccount)).thenReturn(savedAccount);

    // WHEN
    AccountInfoDto actualAccount = accountServiceOnTest.update(accountUpdateDto, existingUser);

    // THEN
    assertEquals(expectedAccount, actualAccount);
  }

  @Test
  @DisplayName("Fail to update bank account if bank account does not exist")
  void updateAccountFailedIfAccountNotExists() {
    // GIVEN
    UUID notExistingAccountId = UUID.randomUUID();
    BigDecimal anyBalance = BigDecimal.valueOf(33.33);
    String anyCurCharCode = "EUR";

    AccountUpdateDto accountUpdateDto =
        new AccountUpdateDto(notExistingAccountId, anyBalance, anyCurCharCode);

    when(accountRepositoryMock.findById(notExistingAccountId)).thenReturn(Optional.empty());

    // WHEN // THEN
    assertThrows(
        BankAccountNotFoundException.class,
        () -> accountServiceOnTest.update(accountUpdateDto, new UserEntity()));
  }

  @Test
  @DisplayName("Fail to update bank account if currency does not exist")
  void updateAccountFailedIfCurrencyNotExists() {
    // GIVEN
    UserEntity existingUser = new UserEntity();
    existingUser.setId(UUID.randomUUID());

    UUID existingAccountId = UUID.randomUUID();
    BankAccountEntity existingAccount = new BankAccountEntity();
    existingAccount.setId(existingAccountId);
    existingAccount.setUser(existingUser);

    BigDecimal anyBalance = BigDecimal.valueOf(33.33);
    String notExistingCurCharCode = "EUR";

    AccountUpdateDto accountUpdateDto =
        new AccountUpdateDto(existingAccountId, anyBalance, notExistingCurCharCode);

    when(accountRepositoryMock.findById(existingAccountId))
        .thenReturn(Optional.of(existingAccount));
    when(currencyRepositoryMock.findByCharCode(notExistingCurCharCode))
        .thenReturn(Optional.empty());

    // WHEN // THEN
    assertThrows(
        CurrencyNotFoundException.class,
        () -> accountServiceOnTest.update(accountUpdateDto, existingUser));
  }

  @Test
  @DisplayName("Change currency of bank account successfully")
  void changeCurrencyOfAccount() {
    // GIVEN
    UserEntity existingUser = new UserEntity();
    existingUser.setId(UUID.randomUUID());

    UUID existingAccountId = UUID.randomUUID();
    BigDecimal expectedBalance = BigDecimal.valueOf(33.33);

    BankAccountEntity existingAccount = new BankAccountEntity();
    existingAccount.setId(existingAccountId);
    existingAccount.setBalance(expectedBalance);
    existingAccount.setCurrencyCharCode("OLD");
    existingAccount.setUser(existingUser);

    String expectedCurCharCode = "EUR";
    CurrencyEntity existingCurrency = new CurrencyEntity();
    existingCurrency.setCharCode(expectedCurCharCode);

    BankAccountEntity newAccount = new BankAccountEntity();
    newAccount.setId(existingAccountId);
    newAccount.setBalance(expectedBalance);
    newAccount.setCurrencyCharCode(expectedCurCharCode);
    newAccount.setUser(existingUser);

    AccountInfoDto expectedAccount = accountMapper.toAccountInfoDto(newAccount);

    when(currencyRepositoryMock.findByCharCode(expectedCurCharCode))
        .thenReturn(Optional.of(existingCurrency));
    when(accountRepositoryMock.findById(existingAccountId))
        .thenReturn(Optional.of(existingAccount));
    when(currencyServiceMock.changeCurrency(existingAccount, expectedCurCharCode))
        .thenReturn(newAccount);
    when(accountRepositoryMock.save(newAccount)).thenReturn(newAccount);

    // WHEN
    AccountInfoDto actualAccount =
        accountServiceOnTest.changeCurrency(existingAccountId, expectedCurCharCode, existingUser);

    // THEN
    assertEquals(expectedAccount, actualAccount);
  }

  @Test
  @DisplayName("Fail to change currency of bank account if bank account does not exist")
  void changeCurrencyOfAccountFailedIfAccountNotExists() {
    // GIVEN
    UUID notExistingAccountId = UUID.randomUUID();
    String anyCurCharCode = "EUR";
    CurrencyEntity existingCurrency = new CurrencyEntity();
    existingCurrency.setCharCode(anyCurCharCode);

    when(currencyRepositoryMock.findByCharCode(anyCurCharCode))
        .thenReturn(Optional.of(existingCurrency));
    when(accountRepositoryMock.findById(notExistingAccountId)).thenReturn(Optional.empty());

    // WHEN // THEN
    assertThrows(
        BankAccountNotFoundException.class,
        () ->
            accountServiceOnTest.changeCurrency(
                notExistingAccountId, anyCurCharCode, new UserEntity()));
  }

  @Test
  @DisplayName("Fail to change currency of bank account if currency does not exist")
  void changeCurrencyOfAccountFailedIfCurrencyNotExists() {
    // GIVEN
    UUID anyAccountId = UUID.randomUUID();
    String notExistingCurCharCode = "EUR";

    when(currencyRepositoryMock.findByCharCode(notExistingCurCharCode))
        .thenReturn(Optional.empty());

    // WHEN // THEN
    assertThrows(
        CurrencyNotFoundException.class,
        () ->
            accountServiceOnTest.changeCurrency(
                anyAccountId, notExistingCurCharCode, new UserEntity()));
  }

  @Test
  @DisplayName("Find bank account by id successfully")
  void getAccountByIdNotChangeCurrency() {
    // GIVEN
    UserEntity existingUser = new UserEntity();
    existingUser.setId(UUID.randomUUID());
    existingUser.setEmail("email@email");
    existingUser.setName("user name");

    UUID existingAccountId = UUID.randomUUID();
    BigDecimal expectedBalance = BigDecimal.valueOf(33.33);
    String expectedCurCharCode = "USD";

    BankAccountEntity existingAccount = new BankAccountEntity();
    existingAccount.setId(existingAccountId);
    existingAccount.setBalance(expectedBalance);
    existingAccount.setCurrencyCharCode(expectedCurCharCode);
    existingAccount.setUser(existingUser);

    AccountInfoDto expectedAccount = accountMapper.toAccountInfoDto(existingAccount);

    when(accountRepositoryMock.findById(existingAccountId))
        .thenReturn(Optional.of(existingAccount));

    // WHEN
    AccountInfoDto actualAccount =
        accountServiceOnTest.getById(existingAccountId, null, existingUser);

    // THEN
    assertEquals(expectedAccount, actualAccount);
  }

  @Test
  @DisplayName("Find bank account by id and change currency successfully")
  void getAccountByIdAndChangeCurrency() {
    // GIVEN
    UserEntity existingUser = new UserEntity();
    existingUser.setId(UUID.randomUUID());
    existingUser.setEmail("email@email");
    existingUser.setName("user name");

    UUID existingAccountId = UUID.randomUUID();
    BigDecimal expectedBalance = BigDecimal.valueOf(33.33);
    String existingCurCharCode = "EUR";
    String expectedCurCharCode = "USD";

    BankAccountEntity existingAccount = new BankAccountEntity();
    existingAccount.setId(existingAccountId);
    existingAccount.setBalance(expectedBalance);
    existingAccount.setCurrencyCharCode(existingCurCharCode);
    existingAccount.setUser(existingUser);

    BankAccountEntity accountWithNewCur = new BankAccountEntity();
    accountWithNewCur.setId(existingAccountId);
    accountWithNewCur.setBalance(expectedBalance);
    accountWithNewCur.setCurrencyCharCode(expectedCurCharCode);
    accountWithNewCur.setUser(existingUser);

    AccountInfoDto expectedAccount = accountMapper.toAccountInfoDto(accountWithNewCur);

    when(accountRepositoryMock.findById(existingAccountId))
        .thenReturn(Optional.of(existingAccount));
    when(currencyServiceMock.changeCurrency(existingAccount, expectedCurCharCode))
        .thenReturn(accountWithNewCur);

    // WHEN
    AccountInfoDto actualAccount =
        accountServiceOnTest.getById(existingAccountId, expectedCurCharCode, existingUser);

    // THEN
    assertEquals(expectedAccount, actualAccount);
  }

  @Test
  @DisplayName("Fail to find bank account if bank account does not exist")
  void getAccountByIdFailedIfNotFound() {
    // GIVEN
    UUID existingAccountId = UUID.randomUUID();

    when(accountRepositoryMock.findById(existingAccountId)).thenReturn(Optional.empty());

    // WHEN // THEN
    assertThrows(
        BankAccountNotFoundException.class,
        () -> accountServiceOnTest.getById(existingAccountId, null, new UserEntity()));
  }

  @Test
  @DisplayName("Delete bank account by id successfully")
  void deleteAccountById() {
    // GIVEN
    UserEntity existingUser = new UserEntity();
    existingUser.setId(UUID.randomUUID());

    UUID existingAccountId = UUID.randomUUID();
    BigDecimal expectedBalance = BigDecimal.valueOf(33.33);
    String expectedCurCharCode = "USD";

    BankAccountEntity existingAccount = new BankAccountEntity();
    existingAccount.setId(existingAccountId);
    existingAccount.setBalance(expectedBalance);
    existingAccount.setCurrencyCharCode(expectedCurCharCode);
    existingAccount.setUser(existingUser);

    AccountInfoDto expectedAccount = accountMapper.toAccountInfoDto(existingAccount);

    when(accountRepositoryMock.findById(existingAccountId))
        .thenReturn(Optional.of(existingAccount));

    // WHEN
    AccountInfoDto actualAccount = accountServiceOnTest.deleteById(existingAccountId, existingUser);

    // THEN
    assertEquals(expectedAccount, actualAccount);
  }

  @Test
  @DisplayName("Fail to delete bank account if bank account does not exist")
  void deleteAccountByIdFailedIfNotFound() {
    // GIVEN
    UUID existingAccountId = UUID.randomUUID();

    when(accountRepositoryMock.findById(existingAccountId)).thenReturn(Optional.empty());

    // WHEN // THEN
    assertThrows(
        BankAccountNotFoundException.class,
        () -> accountServiceOnTest.deleteById(existingAccountId, new UserEntity()));
  }
}
