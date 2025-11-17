package tk.project.globus.hw.exception;

public class CurrenciesPessimisticLockingException extends RuntimeException {
  public CurrenciesPessimisticLockingException(String message) {
    super(message);
  }
}
