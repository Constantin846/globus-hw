package tk.project.globus.hw.config;

import java.time.Instant;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import tk.project.globus.hw.dto.ErrorResponse;
import tk.project.globus.hw.exception.BankAccountNotFoundException;
import tk.project.globus.hw.exception.CurrencyNotFoundException;
import tk.project.globus.hw.exception.UserConflictException;
import tk.project.globus.hw.exception.UserNotAccessException;
import tk.project.globus.hw.exception.UserNotFoundException;
import tk.project.globus.hw.exception.UserUnauthorizedException;

@Slf4j
@RestControllerAdvice
public class AppExceptionHandler {

  @ResponseStatus(HttpStatus.NOT_FOUND)
  @ExceptionHandler(
      exception = {
        BankAccountNotFoundException.class,
        CurrencyNotFoundException.class,
        UserNotFoundException.class,
      })
  public ErrorResponse handleNotFound(RuntimeException ex) {
    return buildErrorResponse(ex, HttpStatus.NOT_FOUND);
  }

  @ResponseStatus(HttpStatus.CONFLICT)
  @ExceptionHandler(UserConflictException.class)
  public ErrorResponse handleUserConflict(UserConflictException ex) {
    return buildErrorResponse(ex, HttpStatus.CONFLICT);
  }

  @ResponseStatus(HttpStatus.UNAUTHORIZED)
  @ExceptionHandler(UserUnauthorizedException.class)
  public ErrorResponse handleUserUnauthorized(UserUnauthorizedException ex) {
    return buildErrorResponse(ex, HttpStatus.UNAUTHORIZED);
  }

  @ResponseStatus(HttpStatus.FORBIDDEN)
  @ExceptionHandler(UserNotAccessException.class)
  public ErrorResponse handleUserNotAccess(UserNotAccessException ex) {
    return buildErrorResponse(ex, HttpStatus.FORBIDDEN);
  }

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ErrorResponse handlerValidException(MethodArgumentNotValidException ex) {
    String message =
        ex.getFieldErrors().stream()
            .map(
                fieldError ->
                    String.format("%s: %s", fieldError.getField(), fieldError.getDefaultMessage()))
            .collect(Collectors.joining("; "));
    return buildErrorResponse(ex, HttpStatus.BAD_REQUEST, message);
  }

  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ErrorResponse uncaughtExceptionHandler(Exception ex) {
    return buildErrorResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  private ErrorResponse buildErrorResponse(Exception ex, HttpStatus status) {
    return buildErrorResponse(ex, status, ex.getMessage());
  }

  private ErrorResponse buildErrorResponse(Exception ex, HttpStatus status, String message) {
    log.warn("Ошибка [{}]: {}", status.value(), message, ex);
    return new ErrorResponse(
        ex.getClass().getSimpleName(), message, Instant.now(), ex.getStackTrace()[0].getFileName());
  }
}
