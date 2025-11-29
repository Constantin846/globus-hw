package tk.project.globus.hw.utility;

import java.util.Objects;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import tk.project.globus.hw.entity.BankAccountEntity;
import tk.project.globus.hw.entity.UserEntity;
import tk.project.globus.hw.exception.UserNotAccessException;

@Slf4j
@UtilityClass
public class UserAccessChecker {

  public static void checkUserAccess(UserEntity authUser, UserEntity user) {
    if (!Objects.equals(authUser.getId(), user.getId())) {
      String msg =
          String.format(
              "У пользователя с id %s не доступа к пользователю с id %s",
              authUser.getId(), user.getId());
      log.warn(msg);
      throw new UserNotAccessException(msg);
    }
  }

  public static void checkUserAccess(UserEntity authUser, BankAccountEntity account) {
    if (!Objects.equals(authUser.getId(), account.getUser().getId())) {
      String msg =
          String.format(
              "У пользователя с id %s не доступа к банковскому счету с id %s",
              authUser.getId(), account.getId());
      log.warn(msg);
      throw new UserNotAccessException(msg);
    }
  }
}
