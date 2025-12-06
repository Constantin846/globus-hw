package tk.project.globus.hw.dto.user;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "DTO для запроса на создание пользователя")
public record UserCreateDto(
    @NotBlank
        @Size(min = 1, max = 50)
        @Schema(description = "Имя пользователя", requiredMode = REQUIRED)
        String name,
    @NotBlank
        @Email
        @Size(min = 1, max = 50)
        @Schema(description = "Почта пользователя", requiredMode = REQUIRED)
        String email,
    @NotBlank
        @Size(min = 1, max = 50)
        @Schema(description = "Пароль пользователя", requiredMode = REQUIRED)
        String password) {}
