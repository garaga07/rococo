package guru.qa.rococo.service.api;

import guru.qa.rococo.ex.NoRestResponseException;
import guru.qa.rococo.model.CountryJson;
import guru.qa.rococo.model.MuseumJson;
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
public class RestMuseumDataClient {

    private final RestTemplate restTemplate;
    private final String rococoMuseumBaseUri;

    @Autowired
    public RestMuseumDataClient(RestTemplate restTemplate,
                                @Value("${rococo-museum.base-uri}") String rococoMuseumBaseUri) {
        this.restTemplate = restTemplate;
        this.rococoMuseumBaseUri = rococoMuseumBaseUri;
    }

    public @Nonnull MuseumJson getMuseumById(@Nonnull UUID id) {
        URI uri = URI.create(rococoMuseumBaseUri + "/internal/museum/" + id);
        ResponseEntity<MuseumJson> response = restTemplate.exchange(uri, HttpMethod.GET, null, MuseumJson.class);
        return Optional.ofNullable(response.getBody())
                .orElseThrow(() -> new NoRestResponseException("No REST response is given [/internal/museum/{id} GET]"));
    }

    public @Nonnull Page<MuseumJson> getAllMuseums(@Nonnull Pageable pageable, String title) {
        String queryParams = new HttpQueryPaginationAndSort(pageable).string();

        if (title != null && !title.isBlank()) {
            queryParams += "&title=" + UriUtils.encode(title, StandardCharsets.UTF_8);
        }

        URI uri = UriComponentsBuilder.fromHttpUrl(rococoMuseumBaseUri + "/internal/museum?" + queryParams)
                .build()
                .toUri();

        ResponseEntity<RestPage<MuseumJson>> response = restTemplate.exchange(uri, HttpMethod.GET, null,
                new ParameterizedTypeReference<>() {});

        return Optional.ofNullable(response.getBody())
                .orElseThrow(() -> new NoRestResponseException("No REST response is given [/internal/museum GET]"));
    }

    public @Nonnull Page<CountryJson> getAllCountries(@Nonnull Pageable pageable) {
        String queryParams = new HttpQueryPaginationAndSort(pageable).string();

        URI uri = UriComponentsBuilder.fromHttpUrl(rococoMuseumBaseUri + "/internal/country?" + queryParams)
                .build()
                .toUri();

        ResponseEntity<RestPage<CountryJson>> response = restTemplate.exchange(uri, HttpMethod.GET, null,
                new ParameterizedTypeReference<>() {});

        return Optional.ofNullable(response.getBody())
                .orElseThrow(() -> new NoRestResponseException("No REST response is given [/internal/country GET]"));
    }

    public @Nonnull MuseumJson addMuseum(@Nonnull MuseumJson museum) {
        URI uri = URI.create(rococoMuseumBaseUri + "/internal/museum");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<MuseumJson> requestEntity = new HttpEntity<>(museum, headers);

        ResponseEntity<MuseumJson> response = restTemplate.exchange(uri, HttpMethod.POST, requestEntity, MuseumJson.class);
        return Optional.ofNullable(response.getBody())
                .orElseThrow(() -> new NoRestResponseException("No REST response is given [/internal/museum POST]"));
    }

    public @Nonnull MuseumJson updateMuseum(@Nonnull MuseumJson museum) {
        URI uri = URI.create(rococoMuseumBaseUri + "/internal/museum");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<MuseumJson> requestEntity = new HttpEntity<>(museum, headers);

        ResponseEntity<MuseumJson> response = restTemplate.exchange(uri, HttpMethod.PATCH, requestEntity, MuseumJson.class);
        return Optional.ofNullable(response.getBody())
                .orElseThrow(() -> new NoRestResponseException("No REST response is given [/internal/museum PATCH]"));
    }
}