package guru.qa.rococo.service.api;

import guru.qa.rococo.ex.NoRestResponseException;
import guru.qa.rococo.model.UserJson;
import jakarta.annotation.Nonnull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Optional;

@Component
public class RestUserDataClient {

    private final RestTemplate restTemplate;
    private final String rococoUserdataBaseUri;

    @Autowired
    public RestUserDataClient(RestTemplate restTemplate,
                              @Value("${rococo-userdata.base-uri}") String rococoUserdataBaseUri) {
        this.restTemplate = restTemplate;
        this.rococoUserdataBaseUri = rococoUserdataBaseUri;
    }

    public @Nonnull UserJson getUser(@Nonnull String username) {
        URI uri = UriComponentsBuilder.fromHttpUrl(rococoUserdataBaseUri + "/internal/user")
                .queryParam("username", username)
                .build().toUri();

        ResponseEntity<UserJson> response = restTemplate.exchange(uri, HttpMethod.GET, null, UserJson.class);
        return Optional.ofNullable(response.getBody())
                .orElseThrow(() -> new NoRestResponseException("No REST response is given [/user GET]"));
    }

    public @Nonnull UserJson updateUserInfo(@Nonnull UserJson user) {
        URI uri = URI.create(rococoUserdataBaseUri + "/internal/user");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<UserJson> requestEntity = new HttpEntity<>(user, headers);

        ResponseEntity<UserJson> response = restTemplate.exchange(uri, HttpMethod.PATCH, requestEntity, UserJson.class);
        return Optional.ofNullable(response.getBody())
                .orElseThrow(() -> new NoRestResponseException("No REST response is given [/user PATCH]"));
    }
}