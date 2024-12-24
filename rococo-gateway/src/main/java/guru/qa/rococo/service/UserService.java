package guru.qa.rococo.service;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;

@Service
public class UserService {
    private static final String USER_JSON_PATH = "response/get_user.json";

    public String getUserJson() throws IOException {
        var resource = new ClassPathResource(USER_JSON_PATH);
        return Files.readString(resource.getFile().toPath());
    }
}