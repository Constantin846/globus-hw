package tk.project.globus.hw.mapper;

import java.math.BigDecimal;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;
import tk.project.globus.hw.dto.currency.CurrencyDto;
import tk.project.globus.hw.dto.currency.CurrencyInfoDto;
import tk.project.globus.hw.entity.CurrencyEntity;

@Mapper(componentModel = "spring")
public interface CurrencyMapper {

  CurrencyMapper MAPPER = Mappers.getMapper(CurrencyMapper.class);

  @Mapping(target = "id", ignore = true)
  @Mapping(source = "vunitRate", target = "vunitRate", qualifiedByName = "parseStringToBigDecimal")
  CurrencyEntity toCurrencyEntity(CurrencyDto currency);

  List<CurrencyEntity> toCurrencyEntities(List<CurrencyDto> currencies);

  CurrencyInfoDto toCurrencyInfoDto(CurrencyEntity currency);

  @Named("parseStringToBigDecimal")
  static BigDecimal parseStringToBigDecimal(String value) {
    return new BigDecimal(value.replace(',', '.'));
  }
}
