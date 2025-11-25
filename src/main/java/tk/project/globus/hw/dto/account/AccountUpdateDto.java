package tk.project.globus.hw.dto.account;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.UUID;

@Schema(description = "DTO для запроса на изменение банковского счета")
public record AccountUpdateDto(
    @NotNull @Schema(description = "Id банковского счета", requiredMode = REQUIRED) UUID id,
    @Schema(description = "Баланс банковского счета", requiredMode = REQUIRED) BigDecimal balance,
    @Size(min = 3, max = 3)
        @Schema(description = "Валюта банковского счета", requiredMode = REQUIRED)
        String currencyCharCode) {}
