package tk.project.globus.hw.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import tk.project.globus.hw.dto.ErrorResponse;
import tk.project.globus.hw.dto.currency.CurrencyInfoDto;
import tk.project.globus.hw.entity.CurrencyEntity;
import tk.project.globus.hw.exception.CurrencyNotFoundException;

class CurrencyIntegrationTest extends BaseIntegrationTest {

  @Value("${app.controller.endpoints.currencies}")
  private String currencyBasePath;

  private CurrencyEntity existingCurrency;

  @Test
  @SneakyThrows
  void getCurrencyById() {
    // GIVEN
    saveExistingCurrency();

    // WHEN
    String result =
        mockMvc
            .perform(
                get(currencyBasePath + "/" + existingCurrency.getId())
                    .contentType("application/json"))
            .andDo(print())
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

    CurrencyInfoDto actualCurrency = objectMapper.readValue(result, CurrencyInfoDto.class);

    // THEN
    assertCurrencyEquals(existingCurrency, actualCurrency);
  }

  @Test
  @SneakyThrows
  void getCurrencyFailedIfCurrencyNotFound() {
    // WHEN
    String result =
        mockMvc
            .perform(
                get(currencyBasePath + "/" + UUID.randomUUID()).contentType("application/json"))
            .andDo(print())
            .andExpect(status().isNotFound())
            .andReturn()
            .getResponse()
            .getContentAsString();

    ErrorResponse errorResponse = objectMapper.readValue(result, ErrorResponse.class);

    // THEN
    assertEquals(CurrencyNotFoundException.class.getSimpleName(), errorResponse.exceptionName());
  }

  @Test
  @SneakyThrows
  void getCurrencyByCharCode() {
    // GIVEN
    saveExistingCurrency();

    // WHEN
    String result =
        mockMvc
            .perform(
                get(currencyBasePath + "/char-code/" + existingCurrency.getCharCode())
                    .contentType("application/json"))
            .andDo(print())
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

    CurrencyInfoDto actualCurrency = objectMapper.readValue(result, CurrencyInfoDto.class);

    // THEN
    assertCurrencyEquals(existingCurrency, actualCurrency);
  }

  @Test
  @SneakyThrows
  void getCurrencyByCharCodeFailedIfCurrencyNotFound() {
    // WHEN
    String result =
        mockMvc
            .perform(get(currencyBasePath + "/char-code/OTH").contentType("application/json"))
            .andDo(print())
            .andExpect(status().isNotFound())
            .andReturn()
            .getResponse()
            .getContentAsString();

    ErrorResponse errorResponse = objectMapper.readValue(result, ErrorResponse.class);

    // THEN
    assertEquals(CurrencyNotFoundException.class.getSimpleName(), errorResponse.exceptionName());
  }

  @Test
  @SneakyThrows
  void findAllCurrencies() {
    // GIVEN
    CurrencyEntity firstCurrency = new CurrencyEntity();
    firstCurrency.setCharCode("FIR");
    firstCurrency.setName("name of FIR");
    firstCurrency.setVunitRate(BigDecimal.valueOf(12131.23));
    currencyRepository.save(firstCurrency);

    CurrencyEntity secondCurrency = new CurrencyEntity();
    secondCurrency.setCharCode("SEC");
    secondCurrency.setName("name of SEC");
    secondCurrency.setVunitRate(BigDecimal.valueOf(12131423.23));
    currencyRepository.save(secondCurrency);

    CurrencyEntity thirdCurrency = new CurrencyEntity();
    thirdCurrency.setCharCode("THI");
    thirdCurrency.setName("name of THI");
    thirdCurrency.setVunitRate(BigDecimal.valueOf(3.2));
    currencyRepository.save(thirdCurrency);

    CurrencyEntity fourthCurrency = new CurrencyEntity();
    fourthCurrency.setCharCode("FOR");
    fourthCurrency.setName("name of FOR");
    fourthCurrency.setVunitRate(BigDecimal.valueOf(343.2));
    currencyRepository.save(fourthCurrency);

    // WHEN
    String result =
        mockMvc
            .perform(get(currencyBasePath).contentType("application/json"))
            .andDo(print())
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

    List<CurrencyInfoDto> actualCurrencies =
        objectMapper.readValue(result, new TypeReference<List<CurrencyInfoDto>>() {});

    // THEN
    assertThat(actualCurrencies)
        .anySatisfy(
            actualCurrency -> {
              assertCurrencyEquals(firstCurrency, actualCurrency);
            })
        .anySatisfy(
            actualCurrency -> {
              assertCurrencyEquals(secondCurrency, actualCurrency);
            })
        .anySatisfy(
            actualCurrency -> {
              assertCurrencyEquals(thirdCurrency, actualCurrency);
            })
        .anySatisfy(
            actualCurrency -> {
              assertCurrencyEquals(fourthCurrency, actualCurrency);
            })
        .hasSize(4);
  }

  private void assertCurrencyEquals(
      CurrencyEntity expectedCurrency, CurrencyInfoDto actualCurrency) {

    assertEquals(expectedCurrency.getId(), actualCurrency.id());
    assertEquals(expectedCurrency.getCharCode(), actualCurrency.charCode());
    assertEquals(expectedCurrency.getName(), actualCurrency.name());
    assertEquals(
        expectedCurrency.getVunitRate().setScale(2, RoundingMode.HALF_UP),
        actualCurrency.vunitRate().setScale(2, RoundingMode.HALF_UP));
  }

  private void saveExistingCurrency() {
    existingCurrency = new CurrencyEntity();
    existingCurrency.setCharCode("CHR");
    existingCurrency.setName("name of CHR");
    existingCurrency.setVunitRate(BigDecimal.valueOf(99.23));
    currencyRepository.save(existingCurrency);
  }
}
