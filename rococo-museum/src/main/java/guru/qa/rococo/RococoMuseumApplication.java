package guru.qa.rococo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RococoMuseumApplication {

    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(RococoMuseumApplication.class);
        springApplication.run(args);
    }
}