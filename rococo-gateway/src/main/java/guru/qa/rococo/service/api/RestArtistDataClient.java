package guru.qa.rococo.service.api;

import guru.qa.rococo.ex.NoRestResponseException;
import guru.qa.rococo.model.ArtistJson;
import guru.qa.rococo.model.page.RestPage;
import guru.qa.rococo.service.utils.HttpQueryPaginationAndSort;
import jakarta.annotation.Nonnull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.UriUtils;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.UUID;

@Component
public class RestArtistDataClient {

    private final RestTemplate restTemplate;
    private final String rococoArtistBaseUri;

    @Autowired
    public RestArtistDataClient(RestTemplate restTemplate,
                                @Value("${rococo-artist.base-uri}") String rococoArtistBaseUri) {
        this.restTemplate = restTemplate;
        this.rococoArtistBaseUri = rococoArtistBaseUri;
    }

    public @Nonnull Page<ArtistJson> getAllArtists(@Nonnull Pageable pageable, String name) {
        String queryParams = new HttpQueryPaginationAndSort(pageable).string();

        if (name != null && !name.isBlank()) {
            queryParams += "&name=" + UriUtils.encode(name, StandardCharsets.UTF_8);
        }

        URI uri = UriComponentsBuilder.fromHttpUrl(rococoArtistBaseUri + "/internal/artist?" + queryParams)
                .build()
                .toUri();

        ResponseEntity<RestPage<ArtistJson>> response = restTemplate.exchange(uri, HttpMethod.GET, null,
                new ParameterizedTypeReference<>() {
                });

        return Optional.ofNullable(response.getBody())
                .orElseThrow(() -> new NoRestResponseException("No REST response is given [/internal/artist GET]"));
    }

    public @Nonnull ArtistJson getArtistById(@Nonnull UUID id) {
        URI uri = URI.create(rococoArtistBaseUri + "/internal/artist/" + id);
        ResponseEntity<ArtistJson> response = restTemplate.exchange(uri, HttpMethod.GET, null, ArtistJson.class);
        return Optional.ofNullable(response.getBody())
                .orElseThrow(() -> new NoRestResponseException("No REST response is given [/internal/artist/{id} GET]"));
    }

    public @Nonnull ArtistJson saveArtist(@Nonnull ArtistJson artist) {
        URI uri = URI.create(rococoArtistBaseUri + "/internal/artist");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<ArtistJson> requestEntity = new HttpEntity<>(artist, headers);

        ResponseEntity<ArtistJson> response = restTemplate.exchange(uri, HttpMethod.POST, requestEntity, ArtistJson.class);
        return Optional.ofNullable(response.getBody())
                .orElseThrow(() -> new NoRestResponseException("No REST response is given [/internal/artist POST]"));
    }

    public @Nonnull ArtistJson updateArtistInfo(@Nonnull ArtistJson artist) {
        URI uri = URI.create(rococoArtistBaseUri + "/internal/artist");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<ArtistJson> requestEntity = new HttpEntity<>(artist, headers);

        ResponseEntity<ArtistJson> response = restTemplate.exchange(uri, HttpMethod.PATCH, requestEntity, ArtistJson.class);
        return Optional.ofNullable(response.getBody())
                .orElseThrow(() -> new NoRestResponseException("No REST response is given [/internal/artist PATCH]"));
    }
}