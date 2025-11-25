package tk.project.globus.hw.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import tk.project.globus.hw.dto.user.UserCreateDto;
import tk.project.globus.hw.dto.user.UserInfoDto;
import tk.project.globus.hw.dto.user.UserUpdateDto;
import tk.project.globus.hw.service.UserService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("${app.controller.endpoints.users}")
@Tag(name = "UserController", description = "API для работы с пользователем")
public class UserController {

  private final UserService userService;

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(summary = "Создание пользователя")
  public UserInfoDto create(@Valid @RequestBody UserCreateDto user) {
    log.info(
        "Получен запрос на создание пользователя с именем {} и почтой {}.",
        user.name(),
        user.email());

    UserInfoDto savedUser = userService.create(user);

    log.info("Выполнен запрос на создание пользователя: {}", savedUser);
    return savedUser;
  }

  @PatchMapping
  @Operation(summary = "Изменение пользователя")
  public UserInfoDto update(@Valid @RequestBody UserUpdateDto user) {
    log.info("Получен запрос на изменение пользователя: {}.", user);

    UserInfoDto updatedUser = userService.update(user);

    log.info("Выполнен запрос на изменение пользователя: {}.", updatedUser);
    return updatedUser;
  }

  @GetMapping("/{userId}")
  @Operation(summary = "Получение информации о пользователе")
  public UserInfoDto getById(@PathVariable("userId") UUID userId) {
    log.info("Получен запрос на получение информации о пользователе с id {}.", userId);

    UserInfoDto foundUser = userService.getById(userId);

    log.info("Выполнен запрос на получение информации о пользователе: {}.", foundUser);
    return foundUser;
  }

  @DeleteMapping("/{userId}")
  @Operation(summary = "Удаление пользователя")
  public UserInfoDto deleteById(@PathVariable("userId") UUID userId) {
    log.info("Получен запрос на удаление пользователя с id {}.", userId);

    UserInfoDto deletedUser = userService.deleteById(userId);

    log.info("Выполнен запрос на удаление пользователя: {}.", deletedUser);
    return deletedUser;
  }
}
