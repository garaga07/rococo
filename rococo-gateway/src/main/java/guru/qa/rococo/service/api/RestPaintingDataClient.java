package guru.qa.rococo.service.api;

import guru.qa.rococo.ex.NoRestResponseException;
import guru.qa.rococo.model.PaintingJson;
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

    public @Nonnull PaintingJson getPaintingById(@Nonnull UUID id) {
        URI uri = URI.create(rococoPaintingBaseUri + "/internal/painting/" + id);
        ResponseEntity<PaintingJson> response = restTemplate.exchange(uri, HttpMethod.GET, null, PaintingJson.class);
        return Optional.ofNullable(response.getBody())
                .orElseThrow(() -> new NoRestResponseException("No REST response is given [/internal/painting/{id} GET]"));
    }

    public @Nonnull Page<PaintingJson> getAllPaintings(@Nonnull Pageable pageable, String title) {
        String queryParams = new HttpQueryPaginationAndSort(pageable).string();

        if (title != null && !title.isBlank()) {
            queryParams += "&title=" + UriUtils.encode(title, StandardCharsets.UTF_8);
        }

        URI uri = UriComponentsBuilder.fromHttpUrl(rococoPaintingBaseUri + "/internal/painting?" + queryParams)
                .build()
                .toUri();

        ResponseEntity<RestPage<PaintingJson>> response = restTemplate.exchange(uri, HttpMethod.GET, null,
                new ParameterizedTypeReference<>() {
                });

        return Optional.ofNullable(response.getBody())
                .orElseThrow(() -> new NoRestResponseException("No REST response is given [/internal/painting GET]"));
    }

    public @Nonnull Page<PaintingJson> getPaintingsByAuthorId(@Nonnull UUID authorId, @Nonnull Pageable pageable) {
        String queryParams = new HttpQueryPaginationAndSort(pageable).string();

        URI uri = UriComponentsBuilder.fromHttpUrl(rococoPaintingBaseUri + "/internal/painting/author/" + authorId + "?" + queryParams)
                .build()
                .toUri();

        ResponseEntity<RestPage<PaintingJson>> response = restTemplate.exchange(uri, HttpMethod.GET, null,
                new ParameterizedTypeReference<>() {
                });

        return Optional.ofNullable(response.getBody())
                .orElseThrow(() -> new NoRestResponseException("No REST response is given [/internal/painting/author/{id} GET]"));
    }

    public @Nonnull PaintingJson addPainting(@Nonnull PaintingJson painting) {
        URI uri = URI.create(rococoPaintingBaseUri + "/internal/painting");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<PaintingJson> requestEntity = new HttpEntity<>(painting, headers);

        ResponseEntity<PaintingJson> response = restTemplate.exchange(uri, HttpMethod.POST, requestEntity, PaintingJson.class);
        return Optional.ofNullable(response.getBody())
                .orElseThrow(() -> new NoRestResponseException("No REST response is given [/internal/painting POST]"));
    }

    public @Nonnull PaintingJson updatePainting(@Nonnull PaintingJson painting) {
        URI uri = URI.create(rococoPaintingBaseUri + "/internal/painting");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<PaintingJson> requestEntity = new HttpEntity<>(painting, headers);

        ResponseEntity<PaintingJson> response = restTemplate.exchange(uri, HttpMethod.PATCH, requestEntity, PaintingJson.class);
        return Optional.ofNullable(response.getBody())
                .orElseThrow(() -> new NoRestResponseException("No REST response is given [/internal/painting PATCH]"));
    }
}