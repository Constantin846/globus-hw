package tk.project.globus.hw.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mock.web.MockHttpSession;
import tk.project.globus.hw.dto.ErrorResponse;
import tk.project.globus.hw.dto.user.UserCreateDto;
import tk.project.globus.hw.dto.user.UserInfoDto;
import tk.project.globus.hw.dto.user.UserUpdateDto;
import tk.project.globus.hw.entity.UserEntity;
import tk.project.globus.hw.exception.UserConflictException;
import tk.project.globus.hw.exception.UserNotAccessException;
import tk.project.globus.hw.exception.UserNotFoundException;
import tk.project.globus.hw.exception.UserUnauthorizedException;

class UserIntegrationTest extends BaseIntegrationTest {

  @Value("${app.controller.endpoints.users}")
  private String userPath;

  @Value("${app.controller.endpoints.registration}")
  private String registrationPath;

  private UserEntity existingUser;

  private void saveExistingUser() {
    existingUser = new UserEntity();
    existingUser.setName("existing name");
    existingUser.setEmail("existing_email@mail");
    existingUser.setPassword("password");
    userRepository.save(existingUser);
  }

  @Test
  @SneakyThrows
  void createUser() {
    // GIVEN
    String expectedUserName = "name";
    String expectedUserEmail = "email@mail";
    String password = "password";
    UserCreateDto userCreateDto = new UserCreateDto(expectedUserName, expectedUserEmail, password);

    // WHEN
    String result =
        mockMvc
            .perform(
                post(registrationPath)
                    .contentType("application/json")
                    .content(objectMapper.writeValueAsString(userCreateDto)))
            .andDo(print())
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse()
            .getContentAsString();

    UserInfoDto actualUser = objectMapper.readValue(result, UserInfoDto.class);

    // THEN
    assertNotNull(actualUser.id());
    assertEquals(expectedUserName, actualUser.name());
    assertEquals(expectedUserEmail, actualUser.email());
  }

  @Test
  @SneakyThrows
  void createUserFailedIfUserEmailAlreadyExists() {
    // GIVEN
    saveExistingUser();
    UserCreateDto userCreateDto = new UserCreateDto("other name", existingUser.getEmail(), "pass");

    // WHEN
    String result =
        mockMvc
            .perform(
                post(registrationPath)
                    .contentType("application/json")
                    .content(objectMapper.writeValueAsString(userCreateDto)))
            .andDo(print())
            .andExpect(status().isConflict())
            .andReturn()
            .getResponse()
            .getContentAsString();

    ErrorResponse errorResponse = objectMapper.readValue(result, ErrorResponse.class);

    // THEN
    assertEquals(UserConflictException.class.getSimpleName(), errorResponse.exceptionName());
  }

  @Test
  @SneakyThrows
  void updateUser() {
    // GIVEN
    saveExistingUser();
    String expectedUserName = "new name";
    String expectedUserEmail = "new_email@mail";
    UserUpdateDto userUpdateDto =
        new UserUpdateDto(existingUser.getId(), expectedUserName, expectedUserEmail);

    // WHEN
    String result =
        mockMvc
            .perform(
                patch(userPath)
                    .header(userEmailHeaderKey, existingUser.getEmail())
                    .header(passwordHeaderKey, existingUser.getPassword())
                    .contentType("application/json")
                    .content(objectMapper.writeValueAsString(userUpdateDto)))
            .andDo(print())
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

    UserInfoDto actualUser = objectMapper.readValue(result, UserInfoDto.class);

    // THEN
    assertEquals(existingUser.getId(), actualUser.id());
    assertEquals(expectedUserName, actualUser.name());
    assertEquals(expectedUserEmail, actualUser.email());
  }

  @Test
  @SneakyThrows
  void updateUserFailedIfUserNotAccess() {
    // GIVEN
    saveExistingUser();

    UserUpdateDto userUpdateDto =
        new UserUpdateDto(existingUser.getId(), "new name", "new_email@mail");

    UserEntity otherUser = new UserEntity();
    otherUser.setName("other name");
    otherUser.setEmail("other_email@mail");
    otherUser.setPassword("other password");
    userRepository.save(otherUser);

    // WHEN
    String result =
        mockMvc
            .perform(
                patch(userPath)
                    .header(userEmailHeaderKey, otherUser.getEmail())
                    .header(passwordHeaderKey, otherUser.getPassword())
                    .contentType("application/json")
                    .content(objectMapper.writeValueAsString(userUpdateDto)))
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
  void getUser() {
    // GIVEN
    saveExistingUser();

    // WHEN
    String result =
        mockMvc
            .perform(
                get(userPath)
                    .header(userEmailHeaderKey, existingUser.getEmail())
                    .header(passwordHeaderKey, existingUser.getPassword())
                    .contentType("application/json"))
            .andDo(print())
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

    UserInfoDto actualUser = objectMapper.readValue(result, UserInfoDto.class);

    // THEN
    assertEquals(existingUser.getId(), actualUser.id());
    assertEquals(existingUser.getName(), actualUser.name());
    assertEquals(existingUser.getEmail(), actualUser.email());
  }

  @Test
  @SneakyThrows
  void getUserFailedIfUserNotFound() {
    // GIVEN
    saveExistingUser();

    MockHttpSession session = new MockHttpSession();
    mockMvc
        .perform(
            get(userPath)
                .session(session)
                .header(userEmailHeaderKey, existingUser.getEmail())
                .header(passwordHeaderKey, existingUser.getPassword())
                .contentType("application/json"))
        .andDo(print())
        .andExpect(status().isOk())
        .andReturn()
        .getResponse()
        .getContentAsString();

    userRepository.delete(existingUser);

    // WHEN
    String result =
        mockMvc
            .perform(get(userPath).session(session).contentType("application/json"))
            .andDo(print())
            .andExpect(status().isNotFound())
            .andReturn()
            .getResponse()
            .getContentAsString();

    ErrorResponse errorResponse = objectMapper.readValue(result, ErrorResponse.class);

    // THEN
    assertEquals(UserNotFoundException.class.getSimpleName(), errorResponse.exceptionName());
  }

  @Test
  @SneakyThrows
  void getUserFailedIfUserUnauthorized() {
    // WHEN
    String result =
        mockMvc
            .perform(get(userPath).contentType("application/json"))
            .andDo(print())
            .andExpect(status().isUnauthorized())
            .andReturn()
            .getResponse()
            .getContentAsString();

    ErrorResponse errorResponse = objectMapper.readValue(result, ErrorResponse.class);

    // THEN
    assertEquals(UserUnauthorizedException.class.getSimpleName(), errorResponse.exceptionName());
  }

  @Test
  @SneakyThrows
  void getUserFailedIfUserEmailIncorrect() {
    // GIVEN
    saveExistingUser();

    // WHEN
    String result =
        mockMvc
            .perform(
                get(userPath)
                    .header(userEmailHeaderKey, "incorrect_email@mail.em")
                    .header(passwordHeaderKey, existingUser.getPassword())
                    .contentType("application/json"))
            .andDo(print())
            .andExpect(status().isUnauthorized())
            .andReturn()
            .getResponse()
            .getContentAsString();

    ErrorResponse errorResponse = objectMapper.readValue(result, ErrorResponse.class);

    // THEN
    assertEquals(UserUnauthorizedException.class.getSimpleName(), errorResponse.exceptionName());
  }

  @Test
  @SneakyThrows
  void getUserFailedIfUserPasswordIncorrect() {
    // GIVEN
    saveExistingUser();

    // WHEN
    String result =
        mockMvc
            .perform(
                get(userPath)
                    .header(userEmailHeaderKey, existingUser.getEmail())
                    .header(passwordHeaderKey, "incorrect password")
                    .contentType("application/json"))
            .andDo(print())
            .andExpect(status().isUnauthorized())
            .andReturn()
            .getResponse()
            .getContentAsString();

    ErrorResponse errorResponse = objectMapper.readValue(result, ErrorResponse.class);

    // THEN
    assertEquals(UserUnauthorizedException.class.getSimpleName(), errorResponse.exceptionName());
  }

  @Test
  @SneakyThrows
  void deleteUser() {
    // GIVEN
    saveExistingUser();

    // WHEN
    String result =
        mockMvc
            .perform(
                delete(userPath)
                    .header(userEmailHeaderKey, existingUser.getEmail())
                    .header(passwordHeaderKey, existingUser.getPassword())
                    .contentType("application/json"))
            .andDo(print())
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

    UserInfoDto actualUser = objectMapper.readValue(result, UserInfoDto.class);

    // THEN
    assertEquals(existingUser.getId(), actualUser.id());
    assertEquals(existingUser.getName(), actualUser.name());
    assertEquals(existingUser.getEmail(), actualUser.email());
  }
}
