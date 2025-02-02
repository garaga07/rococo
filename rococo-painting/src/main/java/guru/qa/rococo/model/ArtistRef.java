package guru.qa.rococo.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public record ArtistRef(
        @JsonProperty("id")
        UUID id
) {
}
