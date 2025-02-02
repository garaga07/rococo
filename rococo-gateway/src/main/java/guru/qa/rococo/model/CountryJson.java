package guru.qa.rococo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CountryJson(
        @NotNull(message = "country.id: ID страны обязателен для заполнения")
        @JsonProperty("id")
        UUID id,
        @JsonProperty("name") String name
) {
}