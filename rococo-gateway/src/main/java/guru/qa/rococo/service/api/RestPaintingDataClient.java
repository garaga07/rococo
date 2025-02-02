package guru.qa.rococo.service.api;

import guru.qa.rococo.ex.NoRestResponseException;
import guru.qa.rococo.model.PaintingRequestJson;
import guru.qa.rococo.model.PaintingResponseJson;
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
public class RestPaintingDataClient {

    private final RestTemplate restTemplate;
    private final String rococoPaintingBaseUri;

    @Autowired
    public RestPaintingDataClient(RestTemplate restTemplate,
                                  @Value("${rococo-painting.base-uri}") String rococoPaintingBaseUri) {
        this.restTemplate = restTemplate;
        this.rococoPaintingBaseUri = rococoPaintingBaseUri;
    }

    public @Nonnull PaintingResponseJson getPaintingById(@Nonnull UUID id) {
        URI uri = URI.create(rococoPaintingBaseUri + "/internal/painting/" + id);
        ResponseEntity<PaintingResponseJson> response = restTemplate.exchange(uri, HttpMethod.GET, null, PaintingResponseJson.class);
        return Optional.ofNullable(response.getBody())
                .orElseThrow(() -> new NoRestResponseException("No REST response is given [/internal/painting/{id} GET]"));
    }

    public @Nonnull Page<PaintingResponseJson> getAllPaintings(@Nonnull Pageable pageable, String title) {
        String queryParams = new HttpQueryPaginationAndSort(pageable).string();

        if (title != null && !title.isBlank()) {
            queryParams += "&title=" + UriUtils.encode(title, StandardCharsets.UTF_8);
        }

        URI uri = UriComponentsBuilder.fromHttpUrl(rococoPaintingBaseUri + "/internal/painting?" + queryParams)
                .build()
                .toUri();

        ResponseEntity<RestPage<PaintingResponseJson>> response = restTemplate.exchange(uri, HttpMethod.GET, null,
                new ParameterizedTypeReference<>() {
                });

        return Optional.ofNullable(response.getBody())
                .orElseThrow(() -> new NoRestResponseException("No REST response is given [/internal/painting GET]"));
    }

    public @Nonnull Page<PaintingResponseJson> getPaintingsByAuthorId(@Nonnull UUID authorId, @Nonnull Pageable pageable) {
        String queryParams = new HttpQueryPaginationAndSort(pageable).string();

        URI uri = UriComponentsBuilder.fromHttpUrl(rococoPaintingBaseUri + "/internal/painting/author/" + authorId + "?" + queryParams)
                .build()
                .toUri();

        ResponseEntity<RestPage<PaintingResponseJson>> response = restTemplate.exchange(uri, HttpMethod.GET, null,
                new ParameterizedTypeReference<>() {
                });

        return Optional.ofNullable(response.getBody())
                .orElseThrow(() -> new NoRestResponseException("No REST response is given [/internal/painting/author/{id} GET]"));
    }

    public @Nonnull PaintingResponseJson addPainting(@Nonnull PaintingRequestJson painting) {
        URI uri = URI.create(rococoPaintingBaseUri + "/internal/painting");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<PaintingRequestJson> requestEntity = new HttpEntity<>(painting, headers);

        ResponseEntity<PaintingResponseJson> response = restTemplate.exchange(uri, HttpMethod.POST, requestEntity, PaintingResponseJson.class);
        return Optional.ofNullable(response.getBody())
                .orElseThrow(() -> new NoRestResponseException("No REST response is given [/internal/painting POST]"));
    }

    public @Nonnull PaintingResponseJson updatePainting(@Nonnull PaintingRequestJson painting) {
        URI uri = URI.create(rococoPaintingBaseUri + "/internal/painting");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<PaintingRequestJson> requestEntity = new HttpEntity<>(painting, headers);

        ResponseEntity<PaintingResponseJson> response = restTemplate.exchange(uri, HttpMethod.PATCH, requestEntity, PaintingResponseJson.class);
        return Optional.ofNullable(response.getBody())
                .orElseThrow(() -> new NoRestResponseException("No REST response is given [/internal/painting PATCH]"));
    }
}