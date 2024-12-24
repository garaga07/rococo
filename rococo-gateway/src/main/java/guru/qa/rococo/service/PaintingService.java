package guru.qa.rococo.service;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;

@Service
public class PaintingService {
    private static final String ALL_PAINTINGS_PATH = "response/get_all_painting.json";
    private static final String PAINTING_BY_AUTHOR_1 = "response/get_painting_by_artist_5a486b2f-c361-459e-bd3f-60692a635ea9.json";
    private static final String PAINTING_BY_AUTHOR_2 = "response/get_painting_by_artist_19bbbbb8-b687-4eec-8ba0-c8917c0a58a3.json";
    private static final String PAINTING_BY_AUTHOR_3 = "response/get_painting_by_artist_104f76ce-0508-49d4-8967-fdf1ebb8cf45.json";
    private static final String PAINTING_BY_ID_1 = "response/get_painting_by_id_9450ff00-0c8c-4a37-9eeb-a1d0b5c3d85b.json";
    private static final String PAINTING_BY_ID_2 = "response/get_painting_by_id_40433774-e548-4504-86ab-cafd25c6abca.json";

    public String getAllPaintingJson() throws IOException {
        var resource = new ClassPathResource(ALL_PAINTINGS_PATH);
        return Files.readString(resource.getFile().toPath());
    }

    public String getPaintingByAuthorJson(String authorId) throws IOException {
        String filePath = switch (authorId) {
            case "5a486b2f-c361-459e-bd3f-60692a635ea9" -> PAINTING_BY_AUTHOR_1;
            case "19bbbbb8-b687-4eec-8ba0-c8917c0a58a3" -> PAINTING_BY_AUTHOR_2;
            case "104f76ce-0508-49d4-8967-fdf1ebb8cf45" -> PAINTING_BY_AUTHOR_3;
            default -> throw new IllegalArgumentException("Author ID not found: " + authorId);
        };

        var resource = new ClassPathResource(filePath);
        return Files.readString(resource.getFile().toPath());
    }

    public String getPaintingByIdJson(String paintingId) throws IOException {
        String filePath = switch (paintingId) {
            case "9450ff00-0c8c-4a37-9eeb-a1d0b5c3d85b" -> PAINTING_BY_ID_1;
            case "40433774-e548-4504-86ab-cafd25c6abca" -> PAINTING_BY_ID_2;
            default -> throw new IllegalArgumentException("Painting ID not found: " + paintingId);
        };

        var resource = new ClassPathResource(filePath);
        return Files.readString(resource.getFile().toPath());
    }
}