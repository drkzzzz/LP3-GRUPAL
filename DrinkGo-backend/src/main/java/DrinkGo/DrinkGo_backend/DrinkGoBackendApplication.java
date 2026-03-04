package DrinkGo.DrinkGo_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "DrinkGo.DrinkGo_backend.repository")
@EnableAsync
public class DrinkGoBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(DrinkGoBackendApplication.class, args);
	}

}
