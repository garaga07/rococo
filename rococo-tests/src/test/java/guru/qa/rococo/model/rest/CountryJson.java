package guru.qa.rococo.model.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import guru.qa.rococo.data.entity.museum.CountryEntity;

import javax.annotation.Nonnull;
import java.util.UUID;

public record CountryJson(
        @JsonProperty("id") UUID id,
        @JsonProperty("name") String name
) {
    public static @Nonnull CountryJson fromEntity(@Nonnull CountryEntity entity) {
        return new CountryJson(
                entity.getId(),
                entity.getName()
        );
    }
}
