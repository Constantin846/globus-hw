package tk.project.globus.hw.kafka.event;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.SIMPLE_NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "event",
    visible = true)
@JsonSubTypes({
  @JsonSubTypes.Type(name = "SAVE_CURRENCY", value = CurrencySaveEvent.class),
})
public interface EventSource {

  Event getEvent();
}
