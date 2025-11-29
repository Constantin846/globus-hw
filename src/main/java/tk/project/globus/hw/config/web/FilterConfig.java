package tk.project.globus.hw.config.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tk.project.globus.hw.filter.SecurityFilter;

@Configuration
public class FilterConfig {

  @Value("${app.controller.endpoints.users}")
  private String usersEndpoint;

  @Value("${app.controller.endpoints.accounts}")
  private String accountsEndpoint;

  @Bean
  public FilterRegistrationBean<SecurityFilter> appFilterRegistration(SecurityFilter filter) {
    FilterRegistrationBean<SecurityFilter> registration = new FilterRegistrationBean<>();
    registration.setFilter(filter);
    registration.addUrlPatterns(usersEndpoint + "/*", accountsEndpoint + "/*");
    return registration;
  }
}
