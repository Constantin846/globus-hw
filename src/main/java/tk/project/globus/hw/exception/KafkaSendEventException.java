package tk.project.globus.hw.exception;

public class KafkaSendEventException extends RuntimeException {
  public KafkaSendEventException(String message) {
    super(message);
  }
}
