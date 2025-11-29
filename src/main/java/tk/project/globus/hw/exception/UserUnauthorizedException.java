package tk.project.globus.hw.exception;

public class UserUnauthorizedException extends RuntimeException {
  public UserUnauthorizedException(String message) {
    super(message);
  }
}
