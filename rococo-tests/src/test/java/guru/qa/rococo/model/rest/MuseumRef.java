package guru.qa.rococo.model.rest;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public record MuseumRef(
        @JsonProperty("id")
        UUID id
) {
}