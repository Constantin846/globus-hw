package tk.project.globus.hw.service;

import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;
import tk.project.globus.hw.entity.UserEntity;
import tk.project.globus.hw.exception.UserUnauthorizedException;
import tk.project.globus.hw.repository.UserRepository;

@Slf4j
@Service
@SessionScope
@RequiredArgsConstructor
public class AuthenticationService {

  private UserEntity authenticatedUser;
  private final UserRepository userRepository;

  public UserEntity getAuthenticatedUser() {
    try {
      return Objects.nonNull(authenticatedUser) ? authenticatedUser.clone() : null;

    } catch (CloneNotSupportedException e) {
      String msg =
          String.format(
              "При клонировании пользователя %s возникла ошибка: %s",
              authenticatedUser, e.getMessage());
      log.warn(msg);
      throw new RuntimeException(msg);
    }
  }

  public void loadAuthenticatedUser(String userEmail, String encodedPassword) {
    UserEntity user =
        userRepository
            .findByEmail(userEmail)
            .orElseThrow(
                () ->
                    new UserUnauthorizedException(
                        String.format("Пользователь с почтой %s не найден.", userEmail)));

    if (!Objects.equals(encodedPassword, user.getPassword())) {
      String msg = String.format("Неправильный пароль для пользователя с почтой %s", userEmail);
      log.warn(msg);
      throw new UserUnauthorizedException(msg);
    }
    this.authenticatedUser = user;
  }
}
