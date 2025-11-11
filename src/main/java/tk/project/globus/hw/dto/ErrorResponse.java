package tk.project.globus.hw.dto;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;

@Schema(description = "DTO для информации об ошибке")
public record ErrorResponse(
    @Schema(description = "Имя ошибки", requiredMode = REQUIRED) String exceptionName,
    @Schema(description = "Сообщение ошибки", requiredMode = REQUIRED) String errorMessage,
    @Schema(description = "Время ошибки", requiredMode = REQUIRED) Instant timestamp,
    @Schema(description = "Имя класса, в котором произошла ошибка", requiredMode = REQUIRED)
        String className) {}
