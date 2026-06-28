package LIBRO.libro;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;

import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableAsync
@EnableScheduling
public class LibroApplication {

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

	public static void main(String[] args) {
		SpringApplication.run(LibroApplication.class, args);
	}

}
