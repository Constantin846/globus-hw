package tk.project.globus.hw.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tk.project.globus.hw.annotation.AuthenticatedUser;
import tk.project.globus.hw.dto.user.UserInfoDto;
import tk.project.globus.hw.dto.user.UserUpdateDto;
import tk.project.globus.hw.entity.UserEntity;
import tk.project.globus.hw.service.UserService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("${app.controller.endpoints.users}")
@Tag(name = "UserController", description = "API для работы с пользователем")
public class UserController {

  private final UserService userService;

  @PatchMapping
  @Operation(summary = "Изменение пользователя")
  public UserInfoDto update(
      @Valid @RequestBody UserUpdateDto user, @AuthenticatedUser UserEntity authUser) {

    log.info("Получен запрос на изменение пользователя: {}.", user);

    UserInfoDto updatedUser = userService.update(user, authUser);

    log.info("Выполнен запрос на изменение пользователя: {}.", updatedUser);
    return updatedUser;
  }

  @GetMapping
  @Operation(summary = "Получение информации о пользователе")
  public UserInfoDto getAuth(@AuthenticatedUser UserEntity authUser) {
    log.info("Получен запрос на получение информации о пользователе с id {}.", authUser.getId());

    UserInfoDto foundUser = userService.getAuthUser(authUser);

    log.info("Выполнен запрос на получение информации о пользователе: {}.", foundUser);
    return foundUser;
  }

  @DeleteMapping
  @Operation(summary = "Удаление пользователя")
  public UserInfoDto deleteAuth(@AuthenticatedUser UserEntity authUser) {
    log.info("Получен запрос на удаление пользователя с id {}.", authUser.getId());

    UserInfoDto deletedUser = userService.deleteAuthUser(authUser);

    log.info("Выполнен запрос на удаление пользователя: {}.", deletedUser);
    return deletedUser;
  }
}
