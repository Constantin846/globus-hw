package tk.project.globus.hw.exception;

public class UserNotAccessException extends RuntimeException {
  public UserNotAccessException(String message) {
    super(message);
  }
}
