package tk.project.globus.hw.kafka.handler;

import tk.project.globus.hw.kafka.event.Event;
import tk.project.globus.hw.kafka.event.EventSource;

public interface EventHandler<T extends EventSource> {

  Event getEvent();

  void handleEvent(T eventSource);
}
