package tk.project.globus.hw.dto.currency;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.NOT_REQUIRED;
import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

@Schema(description = "DTO для сохранения банковской валюты")
public record CurrencySaveDto(
    @NotBlank
        @Size(min = 3, max = 3)
        @Schema(description = "Символьный код банковской валюты", requiredMode = REQUIRED)
        String charCode,
    @Schema(description = "Имя банковской валюты", requiredMode = NOT_REQUIRED) String name,
    @Schema(description = "Курс банковской валюты к рублю", requiredMode = REQUIRED)
        BigDecimal vunitRate) {}
