package tk.project.globus.hw.integration;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import tk.project.globus.hw.repository.BankAccountRepository;
import tk.project.globus.hw.repository.CurrencyRepository;
import tk.project.globus.hw.repository.UserRepository;

@AutoConfigureMockMvc
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BaseIntegrationTest {

  @Value("${app.headers.user-email}")
  protected String userEmailHeaderKey;

  @Value("${app.headers.password}")
  protected String passwordHeaderKey;

  @Autowired protected ObjectMapper objectMapper;
  @Autowired protected MockMvc mockMvc;
  @Autowired protected BankAccountRepository accountRepository;
  @Autowired protected CurrencyRepository currencyRepository;
  @Autowired protected UserRepository userRepository;

  @AfterEach
  void clearDatabase() {
    accountRepository.deleteAll();
    userRepository.deleteAll();
    currencyRepository.deleteAll();
  }

  @Test
  void checkRepositoriesNotNull() {
    assertNotNull(accountRepository);
    assertNotNull(currencyRepository);
    assertNotNull(userRepository);
  }
}
