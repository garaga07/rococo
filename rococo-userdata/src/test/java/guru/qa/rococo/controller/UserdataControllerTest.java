package guru.qa.rococo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import guru.qa.rococo.model.UserJson;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class UserdataControllerTest {

    private static final String AVATAR_PLACEHOLDER = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAUA";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper om;

    @Test
    @Sql("/currentUserShouldBeReturned.sql")
    void shouldReturnUserByUsername() throws Exception {
        mockMvc.perform(get("/internal/user")
                        .param("username", "testUser")
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testUser"))
                .andExpect(jsonPath("$.firstname").value("John"))
                .andExpect(jsonPath("$.lastname").value("Doe"))
                .andExpect(jsonPath("$.avatar").value(AVATAR_PLACEHOLDER));
    }

    @Test
    @Sql("/updateUserInfo.sql")
    void shouldUpdateUserInfo() throws Exception {
        UUID fixtureUserId = UUID.fromString("11111111-1111-1111-1111-111111111111");

        UserJson updatedUser = new UserJson(
                fixtureUserId,
                "newUser",
                "John",
                "Doe",
                AVATAR_PLACEHOLDER
        );

        mockMvc.perform(patch("/internal/user")
                        .contentType(APPLICATION_JSON)
                        .content(om.writeValueAsString(updatedUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(fixtureUserId.toString()))
                .andExpect(jsonPath("$.username").value("newUser"))
                .andExpect(jsonPath("$.firstname").value("John"))
                .andExpect(jsonPath("$.lastname").value("Doe"))
                .andExpect(jsonPath("$.avatar").value(AVATAR_PLACEHOLDER));
    }
}