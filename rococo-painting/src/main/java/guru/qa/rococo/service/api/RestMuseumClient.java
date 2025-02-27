package guru.qa.rococo.service.api;

import guru.qa.rococo.ex.NotFoundException;
import guru.qa.rococo.model.MuseumJson;
import jakarta.annotation.Nonnull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;

@Component
public class RestMuseumClient {

    private final RestTemplate restTemplate;
    private final String museumApiUrl;

    public RestMuseumClient(RestTemplate restTemplate,
                            @Value("${rococo-museum.base-uri}") String museumApiUrl) {
        this.restTemplate = restTemplate;
        this.museumApiUrl = museumApiUrl + "/internal/museum";
    }

    public @Nonnull MuseumJson getMuseumById(@Nonnull String museumId) {
        try {
            return Objects.requireNonNull(restTemplate.getForObject(
                    museumApiUrl + "/{id}",
                    MuseumJson.class,
                    museumId
            ));
        } catch (HttpClientErrorException.NotFound e) {
            throw new NotFoundException("Museum not found with id: " + museumId);
        }
    }
}