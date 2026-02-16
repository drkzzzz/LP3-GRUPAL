package DrinkGo.DrinkGo_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "DrinkGo.DrinkGo_backend.repository")
public class DrinkGoBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(DrinkGoBackendApplication.class, args);
	}

}
