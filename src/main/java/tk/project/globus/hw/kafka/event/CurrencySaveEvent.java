package tk.project.globus.hw.kafka.event;

import lombok.Data;
import lombok.NoArgsConstructor;
import tk.project.globus.hw.dto.currency.CurrencySaveDto;

@Data
@NoArgsConstructor
public class CurrencySaveEvent implements EventSource {

  private final Event event = Event.SAVE_CURRENCY;

  private CurrencySaveDto currencySaveDto;

  public CurrencySaveEvent(CurrencySaveDto currencySaveDto) {
    this.currencySaveDto = currencySaveDto;
  }
}
