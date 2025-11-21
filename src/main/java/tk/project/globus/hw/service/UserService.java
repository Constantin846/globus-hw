package tk.project.globus.hw.service;

import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.project.globus.hw.dto.user.UserCreateDto;
import tk.project.globus.hw.dto.user.UserInfoDto;
import tk.project.globus.hw.dto.user.UserUpdateDto;
import tk.project.globus.hw.entity.UserEntity;
import tk.project.globus.hw.exception.UserConflictException;
import tk.project.globus.hw.exception.UserNotFoundException;
import tk.project.globus.hw.mapper.UserMapper;
import tk.project.globus.hw.repository.UserRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

  private final UserMapper userMapper;
  private final UserRepository userRepository;

  public UserInfoDto create(UserCreateDto newUser) {
    throwExceptionIfUserEmailExists(newUser.email());

    UserEntity userEntity = userMapper.toUserEntity(newUser);
    UserEntity savedUser = userRepository.save(userEntity);

    log.debug("Сохранен пользователь: {}.", savedUser);
    return userMapper.toUserInfoDto(savedUser);
  }

  @Transactional
  public UserInfoDto update(UserUpdateDto userUpdateDto) {
    throwExceptionIfUserEmailExists(userUpdateDto.email());

    UserEntity existingUser = getUserById(userUpdateDto.id());
    UserEntity userToUpdate =
        updateUserFields(existingUser, userMapper.toUserEntity(userUpdateDto));

    UserEntity updatedUser = userRepository.save(userToUpdate);

    log.debug("Обновлен пользователь: {}.", updatedUser);
    return userMapper.toUserInfoDto(updatedUser);
  }

  public UserInfoDto getById(UUID userId) {
    UserEntity user = getUserById(userId);

    log.debug("Найден пользователь: {}.", user);
    return userMapper.toUserInfoDto(user);
  }

  public UserInfoDto deleteById(UUID userId) {
    UserEntity user = getUserById(userId);
    userRepository.delete(user);

    log.debug("Удален пользователь: {}.", user);
    return userMapper.toUserInfoDto(user);
  }

  private UserEntity getUserById(UUID userId) {
    return userRepository
        .findById(userId)
        .orElseThrow(
            () ->
                new UserNotFoundException(
                    String.format("Пользователь с id %s не найден.", userId)));
  }

  private void throwExceptionIfUserEmailExists(String email) {
    if (userRepository.findByEmail(email).isPresent()) {
      throw new UserConflictException(
          String.format("Пользователь c почтой %s уже существует.", email));
    }
  }

  private UserEntity updateUserFields(UserEntity oldUser, UserEntity newUser) {
    if (Objects.nonNull(newUser.getName())) {
      oldUser.setName(newUser.getName());
    }
    if (Objects.nonNull(newUser.getEmail())) {
      oldUser.setEmail(newUser.getEmail());
    }
    return oldUser;
  }
}
