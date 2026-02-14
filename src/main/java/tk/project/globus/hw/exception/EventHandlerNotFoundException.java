package tk.project.globus.hw.exception;

import lombok.Getter;

public class EventHandlerNotFoundException extends RuntimeException {

  @Getter private final String eventName;

  public EventHandlerNotFoundException(String message, String eventName) {
    super(message);
    this.eventName = eventName;
  }
}
