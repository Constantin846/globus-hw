package tk.project.globus.hw.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import tk.project.globus.hw.dto.user.UserCreateDto;
import tk.project.globus.hw.dto.user.UserInfoDto;
import tk.project.globus.hw.service.UserService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("${app.controller.endpoints.registration}")
@Tag(name = "RegistrationController", description = "API для регистрации пользователя")
public class RegistrationController {

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
}
