package tk.project.globus.hw.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import tk.project.globus.hw.dto.user.UserCreateDto;
import tk.project.globus.hw.dto.user.UserInfoDto;
import tk.project.globus.hw.dto.user.UserUpdateDto;
import tk.project.globus.hw.entity.UserEntity;
import tk.project.globus.hw.exception.UserConflictException;
import tk.project.globus.hw.exception.UserNotFoundException;
import tk.project.globus.hw.mapper.UserMapper;
import tk.project.globus.hw.repository.UserRepository;
import tk.project.globus.hw.utility.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

  @Spy private UserMapper userMapper = UserMapper.MAPPER;
  @Mock private UserRepository userRepositoryMock;
  @InjectMocks private UserService userServiceOnTest;

  @Test
  @DisplayName("Create user successfully")
  void createUser() {
    // GIVEN
    UUID expectedUserId = UUID.randomUUID();
    String expectedUserName = "name";
    String expectedUserEmail = "email@mail";
    String uncodedPassword = "password";

    UserCreateDto userCreateDto =
        new UserCreateDto(expectedUserName, expectedUserEmail, uncodedPassword);
    UserEntity userToSave = userMapper.toUserEntity(userCreateDto);
    userToSave.setPassword(PasswordEncoder.encode(uncodedPassword));

    UserEntity savedUser = new UserEntity();
    savedUser.setId(expectedUserId);
    savedUser.setName(expectedUserName);
    savedUser.setEmail(expectedUserEmail);

    UserInfoDto expectedUser = userMapper.toUserInfoDto(savedUser);

    when(userRepositoryMock.save(userToSave)).thenReturn(savedUser);

    // WHEN
    UserInfoDto actualUser = userServiceOnTest.create(userCreateDto);

    // THEN
    assertEquals(expectedUser, actualUser);
  }

  @Test
  @DisplayName("Fail to create user if user email already exists")
  void createUserFailedIfEmailAlreadyExists() {
    // GIVEN
    String expectedUserEmail = "email@mail";
    UserCreateDto userCreateDto = new UserCreateDto("some name", expectedUserEmail, "password");

    UserEntity existingUser = new UserEntity();
    existingUser.setEmail(expectedUserEmail);

    when(userRepositoryMock.findByEmail(expectedUserEmail)).thenReturn(Optional.of(existingUser));

    // WHEN // THEN
    assertThrows(UserConflictException.class, () -> userServiceOnTest.create(userCreateDto));
  }

  @Test
  @DisplayName("Update user successfully")
  void updateUser() {
    // GIVEN
    UUID expectedUserId = UUID.randomUUID();
    String expectedUserName = "name";
    String expectedUserEmail = "email@mail";

    UserUpdateDto userUpdateDto =
        new UserUpdateDto(expectedUserId, expectedUserName, expectedUserEmail);
    UserEntity userToSave = userMapper.toUserEntity(userUpdateDto);

    UserEntity savedUser = new UserEntity();
    savedUser.setId(expectedUserId);
    savedUser.setName(expectedUserName);
    savedUser.setEmail(expectedUserEmail);

    UserInfoDto expectedUser = userMapper.toUserInfoDto(savedUser);

    when(userRepositoryMock.findById(expectedUserId)).thenReturn(Optional.of(savedUser));
    when(userRepositoryMock.save(userToSave)).thenReturn(savedUser);

    // WHEN
    UserInfoDto actualUser = userServiceOnTest.update(userUpdateDto, savedUser);

    // THEN
    assertEquals(expectedUser, actualUser);
  }

  @Test
  @DisplayName("Not update user fields to null")
  void notUpdateUserFieldsToNull() {
    // GIVEN
    UUID expectedUserId = UUID.randomUUID();
    String expectedUserName = "name";
    String expectedUserEmail = "email@mail";

    UserUpdateDto userUpdateDto = new UserUpdateDto(expectedUserId, null, null);

    UserEntity userToSave = new UserEntity();
    userToSave.setId(expectedUserId);
    userToSave.setName(expectedUserName);
    userToSave.setEmail(expectedUserEmail);

    UserEntity savedUser = new UserEntity();
    savedUser.setId(expectedUserId);
    savedUser.setName(expectedUserName);
    savedUser.setEmail(expectedUserEmail);

    UserInfoDto expectedUser = userMapper.toUserInfoDto(savedUser);

    when(userRepositoryMock.findById(expectedUserId)).thenReturn(Optional.of(savedUser));
    when(userRepositoryMock.save(userToSave)).thenReturn(savedUser);

    // WHEN
    UserInfoDto actualUser = userServiceOnTest.update(userUpdateDto, savedUser);

    // THEN
    assertEquals(expectedUser, actualUser);
  }

  @Test
  @DisplayName("Fail to update user if user email already exists")
  void updateUserFailedIfEmailAlreadyExists() {
    // GIVEN
    String busyUserEmail = "email@mail";
    UserEntity userOfBusyEmail = new UserEntity();
    userOfBusyEmail.setEmail(busyUserEmail);

    UUID existingUserId = UUID.randomUUID();
    UserUpdateDto userUpdateDto = new UserUpdateDto(existingUserId, "name", busyUserEmail);
    UserEntity existingUser = new UserEntity();
    existingUser.setId(existingUserId);

    when(userRepositoryMock.findById(existingUserId)).thenReturn(Optional.of(existingUser));
    when(userRepositoryMock.findByEmail(busyUserEmail)).thenReturn(Optional.of(userOfBusyEmail));

    // WHEN // THEN
    assertThrows(
        UserConflictException.class, () -> userServiceOnTest.update(userUpdateDto, existingUser));
  }

  @Test
  @DisplayName("Fail to update user if user is not found")
  void updateUserFailedIfUserNotFound() {
    // GIVEN
    UUID expectedUserId = UUID.randomUUID();
    UserUpdateDto userUpdateDto = new UserUpdateDto(expectedUserId, "name", "email@mail");

    when(userRepositoryMock.findById(expectedUserId)).thenReturn(Optional.empty());

    // WHEN // THEN
    assertThrows(
        UserNotFoundException.class,
        () -> userServiceOnTest.update(userUpdateDto, new UserEntity()));
  }

  @Test
  @DisplayName("Find authenticated user by id successfully")
  void getAuthUser() {
    // GIVEN
    UUID existingUserId = UUID.randomUUID();

    UserEntity existingUser = new UserEntity();
    existingUser.setId(existingUserId);
    existingUser.setName("name");
    existingUser.setEmail("email@mail");

    UserInfoDto expectedUser = userMapper.toUserInfoDto(existingUser);

    when(userRepositoryMock.findById(existingUserId)).thenReturn(Optional.of(existingUser));

    // WHEN
    UserInfoDto actualUser = userServiceOnTest.getAuthUser(existingUser);

    // THEN
    assertEquals(expectedUser, actualUser);
  }

  @Test
  @DisplayName("Delete authenticated user by id successfully")
  void deleteAuthUser() {
    // GIVEN
    UUID existingUserId = UUID.randomUUID();

    UserEntity existingUser = new UserEntity();
    existingUser.setId(existingUserId);
    existingUser.setName("name");
    existingUser.setEmail("email@mail");

    UserInfoDto expectedUser = userMapper.toUserInfoDto(existingUser);

    when(userRepositoryMock.findById(existingUserId)).thenReturn(Optional.of(existingUser));

    // WHEN
    UserInfoDto actualUser = userServiceOnTest.deleteAuthUser(existingUser);

    // THEN
    assertEquals(expectedUser, actualUser);
  }
}
