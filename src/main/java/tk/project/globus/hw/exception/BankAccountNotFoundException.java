package tk.project.globus.hw.exception;

public class BankAccountNotFoundException extends RuntimeException {
  public BankAccountNotFoundException(String message) {
    super(message);
  }
}
