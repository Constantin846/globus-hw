package tk.project.globus.hw;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class GlobusApp {

  public static void main(String[] args) {
    SpringApplication.run(GlobusApp.class, args);
  }
}
