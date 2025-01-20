package guru.qa.rococo.service.api;

import guru.qa.rococo.ex.NotFoundException;
import guru.qa.rococo.model.ArtistJson;
import jakarta.annotation.Nonnull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;
import java.util.Optional;

@Component
public class RestArtistClient {

    private final RestTemplate restTemplate;
    private final String artistApiUrl;

    public RestArtistClient(RestTemplate restTemplate,
                            @Value("${rococo-artist.base-uri}") String artistApiUrl) {
        this.restTemplate = restTemplate;
        this.artistApiUrl = artistApiUrl + "/internal/artist";
    }

    public @Nonnull ArtistJson getArtistById(@Nonnull String artistId) {
        try {
            return Objects.requireNonNull(restTemplate.getForObject(
                    artistApiUrl + "/{id}",
                    ArtistJson.class,
                    artistId
            ));
        } catch (HttpClientErrorException.NotFound e) {
            throw new NotFoundException("Artist not found with id: " + artistId);
        }
    }
}