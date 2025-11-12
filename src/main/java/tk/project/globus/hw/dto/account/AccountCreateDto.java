package tk.project.globus.hw.dto.account;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.UUID;

@Schema(description = "DTO для запроса на создание банковского счета")
public record AccountCreateDto(
    @NotNull @Schema(description = "Баланс банковского счета", requiredMode = REQUIRED)
        BigDecimal balance,
    @NotBlank
        @Size(min = 1, max = 20)
        @Schema(description = "Валюта банковского счета", requiredMode = REQUIRED)
        String currencyCharCode,
    @NotNull @Schema(description = "Id пользователя банковского счета", requiredMode = REQUIRED)
        UUID userId) {}
