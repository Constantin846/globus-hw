package tk.project.globus.hw.dto.currency;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import java.util.List;
import lombok.Data;

@Data
@JacksonXmlRootElement(localName = "ValCurs")
public class CurrenciesDto {

  @JacksonXmlProperty(localName = "Date")
  private String date;

  @JacksonXmlProperty(localName = "name")
  private String name;

  @JacksonXmlProperty(localName = "Valute")
  @JacksonXmlElementWrapper(useWrapping = false)
  private List<CurrencyDto> currencies;
}
