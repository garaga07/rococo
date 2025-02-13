package guru.qa.rococo.test.jdbc;

import guru.qa.rococo.jupiter.extension.ArtistExtension;
import guru.qa.rococo.model.rest.ArtistJson;
import guru.qa.rococo.service.impl.ArtistDbClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(ArtistExtension.class)
public class JdbcArtistTest {

    private final ArtistDbClient artistDbClient = new ArtistDbClient();

    @Test
    void createArtistTest() {
        ArtistJson artist = artistDbClient.createArtist(
                new ArtistJson(
                        null,
                        "Leonardo da Vinci",
                        "Italian Renaissance polymath, famous for the Mona Lisa and The Last Supper.",
                        "leonardo_base64_photo"
                )
        );

        System.out.println("Created artist: " + artist);
    }
}