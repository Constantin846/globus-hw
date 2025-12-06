package tk.project.globus.hw.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import tk.project.globus.hw.dto.ErrorResponse;
import tk.project.globus.hw.exception.UserUnauthorizedException;
import tk.project.globus.hw.service.AuthenticationService;

@Component
@RequiredArgsConstructor
public class SecurityFilter extends OncePerRequestFilter {

  @Value("${app.headers.user-email}")
  private String userEmailKey;

  @Value("${app.headers.password}")
  private String passwordKey;

  private final AuthenticationService authenticationService;
  private final ObjectMapper objectMapper;

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    try {
      if (Objects.isNull(authenticationService.getAuthenticatedUser())) {
        String userEmail = request.getHeader(userEmailKey);
        String encodedPassword = request.getHeader(passwordKey);
        authenticationService.loadAuthenticatedUser(userEmail, encodedPassword);
      }

      filterChain.doFilter(request, response);

    } catch (UserUnauthorizedException ex) {
      ErrorResponse body =
          new ErrorResponse(
              ex.getClass().getSimpleName(),
              ex.getMessage(),
              Instant.now(),
              ex.getStackTrace()[0].getFileName());

      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      response.setContentType("application/json");
      response.setCharacterEncoding("UTF-8");
      response.getWriter().write(objectMapper.writeValueAsString(body));
      response.getWriter().flush();
    }
  }
}
