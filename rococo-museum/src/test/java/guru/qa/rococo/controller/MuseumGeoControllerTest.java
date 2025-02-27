package guru.qa.rococo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import guru.qa.rococo.model.CountryJson;
import guru.qa.rococo.model.GeoJson;
import guru.qa.rococo.model.MuseumJson;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class MuseumGeoControllerTest {

    private static final String PHOTO_PLACEHOLDER = "data:image/jpeg;base64,/9j/4AAQSkZ";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper om;

    @Test
    @Sql("/countryListShouldBeReturned.sql")
    void countryListShouldBeReturned() throws Exception {
        mockMvc.perform(get("/internal/country"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].name").value("France"))
                .andExpect(jsonPath("$.content[1].name").value("Italy"));
    }

    @Test
    @Sql("/museumListShouldBeReturned.sql")
    void museumListShouldBeReturned() throws Exception {
        mockMvc.perform(get("/internal/museum"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].id").value("555e4567-e89b-12d3-a456-426614174000"))
                .andExpect(jsonPath("$.content[0].title").value("Louvre Museum"))
                .andExpect(jsonPath("$.content[0].geo.city").value("Paris"))
                .andExpect(jsonPath("$.content[0].geo.country.name").value("France"))
                .andExpect(jsonPath("$.content[1].id").value("666e4567-e89b-12d3-a456-426614174001"))
                .andExpect(jsonPath("$.content[1].title").value("Uffizi Gallery"))
                .andExpect(jsonPath("$.content[1].geo.city").value("Florence"))
                .andExpect(jsonPath("$.content[1].geo.country.name").value("Italy"));
    }

    @Test
    @Sql("/museumShouldBeReturnedById.sql")
    void museumShouldBeReturnedById() throws Exception {
        UUID museumId = UUID.fromString("555e4567-e89b-12d3-a456-426614174000");

        mockMvc.perform(get("/internal/museum/{id}", museumId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(museumId.toString()))
                .andExpect(jsonPath("$.title").value("Louvre Museum"))
                .andExpect(jsonPath("$.description").value("Famous museum in Paris"))
                .andExpect(jsonPath("$.photo").value(PHOTO_PLACEHOLDER))
                .andExpect(jsonPath("$.geo.city").value("Paris"))
                .andExpect(jsonPath("$.geo.country.name").value("France"));
    }

    @Test
    void notFoundExceptionShouldBeReturned() throws Exception {
        UUID nonExistentId = UUID.randomUUID();

        mockMvc.perform(get("/internal/museum/{id}", nonExistentId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.detail").value("id: Музей не найден с id: " + nonExistentId));
    }

    @Test
    @Sql(scripts = {"/setupCountryAndGeo.sql"})
    void museumShouldBeAdded() throws Exception {
        MuseumJson newMuseum = new MuseumJson(
                null,
                "Tate Modern",
                "Modern art museum in London",
                PHOTO_PLACEHOLDER,
                new GeoJson("London", new CountryJson(UUID.fromString("777e4567-e89b-12d3-a456-426614174002"), "United Kingdom"))
        );

        mockMvc.perform(post("/internal/museum")
                        .contentType(APPLICATION_JSON)
                        .content(om.writeValueAsString(newMuseum)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Tate Modern"))
                .andExpect(jsonPath("$.description").value("Modern art museum in London"))
                .andExpect(jsonPath("$.photo").value(PHOTO_PLACEHOLDER))
                .andExpect(jsonPath("$.geo.city").value("London"))
                .andExpect(jsonPath("$.geo.country.name").value("United Kingdom"));
    }

    @Test
    @Sql("/museumShouldBeUpdated.sql")
    void museumShouldBeUpdated() throws Exception {
        UUID fixtureMuseumId = UUID.fromString("555e4567-e89b-12d3-a456-426614174000");

        MuseumJson updatedMuseum = new MuseumJson(
                fixtureMuseumId,
                "Updated Louvre",
                "Updated museum description",
                PHOTO_PLACEHOLDER,
                new GeoJson("Paris", new CountryJson(UUID.fromString("111e4567-e89b-12d3-a456-426614174000"), "France"))
        );

        mockMvc.perform(patch("/internal/museum")
                        .contentType(APPLICATION_JSON)
                        .content(om.writeValueAsString(updatedMuseum)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(fixtureMuseumId.toString()))
                .andExpect(jsonPath("$.title").value("Updated Louvre"))
                .andExpect(jsonPath("$.description").value("Updated museum description"))
                .andExpect(jsonPath("$.photo").value(PHOTO_PLACEHOLDER))
                .andExpect(jsonPath("$.geo.city").value("Paris"))
                .andExpect(jsonPath("$.geo.country.name").value("France"));
    }

    @Test
    void badRequestExceptionShouldBeThrownOnUpdateWithoutId() throws Exception {
        MuseumJson invalidMuseum = new MuseumJson(
                null,
                "Invalid Museum",
                "Should not be updated",
                PHOTO_PLACEHOLDER,
                new GeoJson("Paris", new CountryJson(UUID.randomUUID(), "France"))
        );

        mockMvc.perform(patch("/internal/museum")
                        .contentType(APPLICATION_JSON)
                        .content(om.writeValueAsString(invalidMuseum)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.detail").value("id: ID музея обязателен для заполнения"));
    }

    @Test
    @Sql("/museumListShouldBeReturned.sql")
    void museumShouldBeFilteredByTitle() throws Exception {
        mockMvc.perform(get("/internal/museum?title=Louvre"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].id").value("555e4567-e89b-12d3-a456-426614174000"))
                .andExpect(jsonPath("$.content[0].title").value("Louvre Museum"))
                .andExpect(jsonPath("$.content[0].geo.city").value("Paris"))
                .andExpect(jsonPath("$.content[0].geo.country.name").value("France"));
    }
}