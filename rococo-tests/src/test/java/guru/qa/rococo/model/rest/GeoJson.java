package guru.qa.rococo.model.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import guru.qa.rococo.data.entity.museum.GeoEntity;

import javax.annotation.Nonnull;

public record GeoJson(
        @JsonProperty("city") String city,
        @JsonProperty("country") CountryJson country
) {
    public static @Nonnull GeoJson fromEntity(@Nonnull GeoEntity entity) {
        return new GeoJson(
                entity.getCity(),
                CountryJson.fromEntity(entity.getCountry())
        );
    }
}
