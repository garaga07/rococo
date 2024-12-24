package guru.qa.rococo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RococoArtistApplication {

    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(RococoArtistApplication.class);
        springApplication.run(args);
    }
}