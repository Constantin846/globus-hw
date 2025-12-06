package tk.project.globus.hw.config.web;

import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import tk.project.globus.hw.annotation.AuthenticatedUser;
import tk.project.globus.hw.service.AuthenticationService;

@Component
@RequiredArgsConstructor
public class AuthenticatedUserHandlerMethodArgumentResolver
    implements HandlerMethodArgumentResolver {

  private final AuthenticationService authenticationService;

  @Override
  public boolean supportsParameter(MethodParameter parameter) {
    return parameter.hasParameterAnnotation(AuthenticatedUser.class);
  }

  @Override
  public Object resolveArgument(
      MethodParameter parameter,
      ModelAndViewContainer mavContainer,
      NativeWebRequest webRequest,
      WebDataBinderFactory binderFactory)
      throws Exception {

    return authenticationService.getAuthenticatedUser();
  }
}
