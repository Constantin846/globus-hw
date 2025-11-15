package tk.project.globus.hw.dto.user;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.NOT_REQUIRED;
import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;

@Schema(description = "DTO для запроса на изменения пользователя")
public record UserUpdateDto(
    @NotNull @Schema(description = "Id пользователя", requiredMode = REQUIRED) UUID id,
    @Size(min = 1, max = 50) @Schema(description = "Имя пользователя", requiredMode = NOT_REQUIRED)
        String name,
    @Size(min = 1, max = 50)
        @Email
        @Schema(description = "Почта пользователя", requiredMode = NOT_REQUIRED)
        String email) {}
