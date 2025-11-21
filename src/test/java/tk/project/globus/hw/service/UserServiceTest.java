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

    UserCreateDto userCreateDto = new UserCreateDto(expectedUserName, expectedUserEmail);
    UserEntity userToSave = userMapper.toUserEntity(userCreateDto);

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
    UserCreateDto userCreateDto = new UserCreateDto("some name", expectedUserEmail);

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
    UserInfoDto actualUser = userServiceOnTest.update(userUpdateDto);

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
    UserInfoDto actualUser = userServiceOnTest.update(userUpdateDto);

    // THEN
    assertEquals(expectedUser, actualUser);
  }

  @Test
  @DisplayName("Fail to update user if user email already exists")
  void updateUserFailedIfEmailAlreadyExists() {
    // GIVEN
    String expectedUserEmail = "email@mail";
    UserUpdateDto userUpdateDto = new UserUpdateDto(UUID.randomUUID(), "name", expectedUserEmail);

    UserEntity existingUser = new UserEntity();
    existingUser.setEmail(expectedUserEmail);

    when(userRepositoryMock.findByEmail(expectedUserEmail)).thenReturn(Optional.of(existingUser));

    // WHEN // THEN
    assertThrows(UserConflictException.class, () -> userServiceOnTest.update(userUpdateDto));
  }

  @Test
  @DisplayName("Fail to update user if user is not found")
  void updateUserFailedIfUserNotFound() {
    // GIVEN
    UUID expectedUserId = UUID.randomUUID();
    UserUpdateDto userUpdateDto = new UserUpdateDto(expectedUserId, "name", "email@mail");

    when(userRepositoryMock.findById(expectedUserId)).thenReturn(Optional.empty());

    // WHEN // THEN
    assertThrows(UserNotFoundException.class, () -> userServiceOnTest.update(userUpdateDto));
  }

  @Test
  @DisplayName("Find user by id successfully")
  void getUserById() {
    // GIVEN
    UUID existingUserId = UUID.randomUUID();

    UserEntity existingUser = new UserEntity();
    existingUser.setId(existingUserId);
    existingUser.setName("name");
    existingUser.setEmail("email@mail");

    UserInfoDto expectedUser = userMapper.toUserInfoDto(existingUser);

    when(userRepositoryMock.findById(existingUserId)).thenReturn(Optional.of(existingUser));

    // WHEN
    UserInfoDto actualUser = userServiceOnTest.getById(existingUserId);

    // THEN
    assertEquals(expectedUser, actualUser);
  }

  @Test
  @DisplayName("Fail to get user by id if user is not found")
  void getUserByIdFailedIfUserNotFound() {
    // GIVEN
    UUID existingUserId = UUID.randomUUID();

    when(userRepositoryMock.findById(existingUserId)).thenReturn(Optional.empty());

    // WHEN // THEN
    assertThrows(UserNotFoundException.class, () -> userServiceOnTest.getById(existingUserId));
  }

  @Test
  @DisplayName("Delete user by id successfully")
  void deleteUserById() {
    // GIVEN
    UUID existingUserId = UUID.randomUUID();

    UserEntity existingUser = new UserEntity();
    existingUser.setId(existingUserId);
    existingUser.setName("name");
    existingUser.setEmail("email@mail");

    UserInfoDto expectedUser = userMapper.toUserInfoDto(existingUser);

    when(userRepositoryMock.findById(existingUserId)).thenReturn(Optional.of(existingUser));

    // WHEN
    UserInfoDto actualUser = userServiceOnTest.deleteById(existingUserId);

    // THEN
    assertEquals(expectedUser, actualUser);
  }

  @Test
  @DisplayName("Fail to delete user by id if user is not found")
  void deleteUserByIdFailedIfUserNotFound() {
    // GIVEN
    UUID existingUserId = UUID.randomUUID();

    when(userRepositoryMock.findById(existingUserId)).thenReturn(Optional.empty());

    // WHEN // THEN
    assertThrows(UserNotFoundException.class, () -> userServiceOnTest.deleteById(existingUserId));
  }
}
