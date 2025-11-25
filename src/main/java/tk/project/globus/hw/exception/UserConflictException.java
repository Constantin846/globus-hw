package tk.project.globus.hw.exception;

public class UserConflictException extends RuntimeException {
  public UserConflictException(String message) {
    super(message);
  }
}
