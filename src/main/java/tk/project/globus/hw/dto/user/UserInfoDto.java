package tk.project.globus.hw.dto.user;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;

@Schema(description = "DTO для информации о пользователе")
public record UserInfoDto(
    @Schema(description = "Id пользователя", requiredMode = REQUIRED) UUID id,
    @Schema(description = "Имя пользователя", requiredMode = REQUIRED) String name,
    @Schema(description = "Почта пользователя", requiredMode = REQUIRED) String email) {}
