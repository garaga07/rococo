package guru.qa.rococo.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import guru.qa.rococo.api.GatewayApi;
import guru.qa.rococo.api.core.RestClient;
import guru.qa.rococo.model.ErrorJson;
import guru.qa.rococo.model.rest.*;
import guru.qa.rococo.model.rest.pageable.RestResponsePage;
import io.qameta.allure.Step;
import retrofit2.Call;
import retrofit2.Response;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;

public class GatewayApiClient extends RestClient {
    private final GatewayApi gatewayApi;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public GatewayApiClient() {
        super(CFG.gatewayUrl());
        this.gatewayApi = create(GatewayApi.class);
    }

    @Step("Send GET /api/artist/{id} request")
    public Response<ArtistJson> getArtistById(@Nonnull String id) {
        return executeRequest(gatewayApi.getArtistById(id));
    }

    @Step("Send GET /api/artist request with pagination")
    public Response<RestResponsePage<ArtistJson>> getAllArtists(int page, int size, @Nullable String name) {
        return executeRequest(gatewayApi.getAllArtists(page, size, name));
    }

    @Step("Send POST /api/artist request")
    public Response<ArtistJson> addArtist(@Nonnull String bearerToken, @Nonnull ArtistJson artist) {
        return executeRequest(gatewayApi.addArtist(bearerToken, artist));
    }

    @Step("Send PATCH /api/artist request")
    public Response<ArtistJson> updateArtist(@Nonnull String bearerToken, @Nonnull ArtistJson artist) {
        return executeRequest(gatewayApi.updateArtist(bearerToken, artist));
    }

    @Step("Send GET /api/museum/{id} request")
    public Response<MuseumJson> getMuseumById(@Nonnull String id) {
        return executeRequest(gatewayApi.getMuseumById(id));
    }

    @Step("Send GET /api/museum request with pagination")
    public Response<RestResponsePage<MuseumJson>> getAllMuseums(int page, int size, @Nullable String title) {
        return executeRequest(gatewayApi.getAllMuseums(page, size, title));
    }

    @Step("Send POST /api/museum request")
    public Response<MuseumJson> addMuseum(@Nonnull String bearerToken, @Nonnull MuseumJson museum) {
        return executeRequest(gatewayApi.addMuseum(bearerToken, museum));
    }

    @Step("Send PATCH /api/museum request")
    public Response<MuseumJson> updateMuseum(@Nonnull String bearerToken, @Nonnull MuseumJson museum) {
        return executeRequest(gatewayApi.updateMuseum(bearerToken, museum));
    }

    @Step("Send GET /api/country request with pagination")
    public Response<RestResponsePage<CountryJson>> getAllCountries(int page, int size) {
        return executeRequest(gatewayApi.getAllCountries(page, size));
    }

    @Step("Send GET /api/painting/{id} request")
    public Response<PaintingResponseJson> getPaintingById(@Nonnull String id) {
        return executeRequest(gatewayApi.getPaintingById(id));
    }

    @Step("Send GET /api/painting request with pagination")
    public Response<RestResponsePage<PaintingResponseJson>> getAllPaintings(int page, int size, @Nullable String title) {
        return executeRequest(gatewayApi.getAllPaintings(page, size, title));
    }

    @Step("Send GET /api/painting/author/{authorId} request with pagination")
    public Response<RestResponsePage<PaintingResponseJson>> getPaintingsByAuthorId(@Nonnull String authorId, int page, int size) {
        return executeRequest(gatewayApi.getPaintingsByAuthorId(authorId, page, size));
    }

    @Step("Send POST /api/painting request")
    public Response<PaintingResponseJson> addPainting(@Nonnull String bearerToken, @Nonnull PaintingRequestJson painting) {
        return executeRequest(gatewayApi.addPainting(bearerToken, painting));
    }

    @Step("Send PATCH /api/painting request")
    public Response<PaintingResponseJson> updatePainting(@Nonnull String bearerToken, @Nonnull PaintingRequestJson painting) {
        return executeRequest(gatewayApi.updatePainting(bearerToken, painting));
    }

    @Step("Send GET /api/session/ request")
    public Response<SessionJson> currentSession(@Nonnull String bearerToken) {
        return executeRequest(gatewayApi.currentSession(bearerToken));
    }

    @Step("Send GET /api/user request")
    public Response<UserJson> getUser(@Nonnull String bearerToken) {
        return executeRequest(gatewayApi.getUser(bearerToken));
    }

    @Step("Send PATCH /api/user request")
    public Response<UserJson> updateUser(@Nonnull String bearerToken, @Nonnull UserJson user) {
        return executeRequest(gatewayApi.updateUser(bearerToken, user));
    }

    private <T> Response<T> executeRequest(Call<T> call) {
        try {
            return call.execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
    }

    public ErrorJson parseError(Response<?> response) {
        try {
            if (response.errorBody() != null) {
                return objectMapper.readValue(response.errorBody().string(), ErrorJson.class);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse error response", e);
        }
        return null;
    }
}