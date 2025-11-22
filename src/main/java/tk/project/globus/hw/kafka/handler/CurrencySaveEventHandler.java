package tk.project.globus.hw.kafka.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import tk.project.globus.hw.kafka.event.CurrencySaveEvent;
import tk.project.globus.hw.kafka.event.Event;
import tk.project.globus.hw.service.CurrencyService;

@Slf4j
@Component
@RequiredArgsConstructor
public class CurrencySaveEventHandler implements EventHandler<CurrencySaveEvent> {

  private static final Event EVENT = Event.SAVE_CURRENCY;

  private final CurrencyService currencyService;

  @Override
  public Event getEvent() {
    return EVENT;
  }

  @Override
  public void handleEvent(CurrencySaveEvent currencySaveEvent) {
    currencyService.save(currencySaveEvent.getCurrencySaveDto());
  }
}
