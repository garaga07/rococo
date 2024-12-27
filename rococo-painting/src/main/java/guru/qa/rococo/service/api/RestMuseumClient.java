package guru.qa.rococo.service.api;

import guru.qa.rococo.model.MuseumJson;
import jakarta.annotation.Nonnull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

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
        return Optional.ofNullable(
                restTemplate.getForObject(
                        museumApiUrl + "/{id}",
                        MuseumJson.class,
                        museumId
                )
        ).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Museum not found with ID: " + museumId));
    }
}