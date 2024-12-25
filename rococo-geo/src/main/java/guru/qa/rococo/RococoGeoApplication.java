package guru.qa.rococo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RococoGeoApplication {

    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(RococoGeoApplication.class);
        springApplication.run(args);
    }
}