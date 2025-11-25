package tk.project.globus.hw.dto.currency;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.util.UUID;

@Schema(description = "DTO для информации о банковской валюте")
public record CurrencyInfoDto(
    @Schema(description = "Id банковской валюты", requiredMode = REQUIRED) UUID id,
    @Schema(description = "Символьный код банковской валюты", requiredMode = REQUIRED)
        String charCode,
    @Schema(description = "Имя банковской валюты", requiredMode = REQUIRED) String name,
    @Schema(description = "Курс банковской валюты к рублю", requiredMode = REQUIRED)
        BigDecimal vunitRate) {}
