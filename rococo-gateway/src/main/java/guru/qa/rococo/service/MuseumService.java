package guru.qa.rococo.service;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;

@Service
public class MuseumService {
    private static final String MUSEUM_JSON_PATH = "response/get_all_museum.json";
    private static final String MUSEUM_ID_1 = "response/get_museum_by_id_3b785453-0d5b-4328-8380-5f226cb4dd5a.json";
    private static final String MUSEUM_ID_2 = "response/get_museum_by_id_457fa929-4aa6-4772-86e6-57382d212144.json";

    public String getAllMuseumJson() throws IOException {
        var resource = new ClassPathResource(MUSEUM_JSON_PATH);
        return Files.readString(resource.getFile().toPath());
    }

    public String getMuseumById(String id) throws IOException {
        String path = switch (id) {
            case "3b785453-0d5b-4328-8380-5f226cb4dd5a" -> MUSEUM_ID_1;
            case "457fa929-4aa6-4772-86e6-57382d212144" -> MUSEUM_ID_2;
            default -> throw new IllegalArgumentException("Museum not found for ID: " + id);
        };

        var resource = new ClassPathResource(path);
        return Files.readString(resource.getFile().toPath());
    }
}