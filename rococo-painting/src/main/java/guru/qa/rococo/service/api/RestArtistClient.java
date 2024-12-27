package guru.qa.rococo.service.api;

import guru.qa.rococo.model.ArtistJson;
import jakarta.annotation.Nonnull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

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
        return Optional.ofNullable(
                restTemplate.getForObject(
                        artistApiUrl + "/{id}",
                        ArtistJson.class,
                        artistId
                )
        ).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Artist not found with id: " + artistId));
    }
}