package guru.qa.rococo.service;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;

@Service
public class ArtistService {
    private static final String ALL_ARTIST_JSON_PATH = "response/get_all_artist.json";
    private static final String ARTIST_BY_ID_1 = "response/get_artist_by_id_19bbbbb8-b687-4eec-8ba0-c8917c0a58a3.json";
    private static final String ARTIST_BY_ID_2 = "response/get_artist_by_id_5a486b2f-c361-459e-bd3f-60692a635ea9.json";
    private static final String ARTIST_BY_ID_3 = "response/get_artist_by_id_104f76ce-0508-49d4-8967-fdf1ebb8cf45.json";

    public String getAllArtistJson() throws IOException {
        var resource = new ClassPathResource(ALL_ARTIST_JSON_PATH);
        return Files.readString(resource.getFile().toPath());
    }

    public String getArtistById(String id) throws IOException {
        String path = switch (id) {
            case "19bbbbb8-b687-4eec-8ba0-c8917c0a58a3" -> ARTIST_BY_ID_1;
            case "5a486b2f-c361-459e-bd3f-60692a635ea9" -> ARTIST_BY_ID_2;
            case "104f76ce-0508-49d4-8967-fdf1ebb8cf45" -> ARTIST_BY_ID_3;
            default -> throw new IllegalArgumentException("Artist not found for ID: " + id);
        };

        var resource = new ClassPathResource(path);
        return Files.readString(resource.getFile().toPath());
    }
}
