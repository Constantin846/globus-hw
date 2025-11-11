package tk.project.globus.hw.dto.account;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.util.UUID;
import tk.project.globus.hw.dto.user.UserInfoDto;

@Schema(description = "DTO для информации о банковском счете")
public record AccountInfoDto(
    @Schema(description = "Id банковского счета", requiredMode = REQUIRED) UUID id,
    @Schema(description = "Баланс банковского счета", requiredMode = REQUIRED) BigDecimal balance,
    @Schema(description = "Валюта банковского счета", requiredMode = REQUIRED)
        String currencyCharCode,
    @Schema(description = "Пользователь банковского счета", requiredMode = REQUIRED)
        UserInfoDto user) {}
