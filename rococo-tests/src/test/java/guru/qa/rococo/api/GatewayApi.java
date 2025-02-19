package guru.qa.rococo.api;

import guru.qa.rococo.model.rest.*;
import guru.qa.rococo.model.rest.pageable.RestResponsePage;
import retrofit2.Call;
import retrofit2.http.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface GatewayApi {

    @GET("api/artist/{id}")
    Call<ArtistJson> getArtistById(@Path("id") @Nonnull String id);

    @GET("api/artist")
    Call<RestResponsePage<ArtistJson>> getAllArtists(@Query("page") int page,
                                                     @Query("size") int size,
                                                     @Query("name") @Nullable String name);

    @POST("api/artist")
    Call<ArtistJson> addArtist(@Header("Authorization") String bearerToken,
                               @Body ArtistJson artist);

    @PATCH("api/artist")
    Call<ArtistJson> updateArtist(@Header("Authorization") String bearerToken,
                                  @Body ArtistJson artist);

    @GET("api/museum/{id}")
    Call<MuseumJson> getMuseumById(@Path("id") @Nonnull String id);

    @GET("api/museum")
    Call<RestResponsePage<MuseumJson>> getAllMuseums(@Query("page") int page,
                                                     @Query("size") int size,
                                                     @Query("title") @Nullable String title);

    @POST("api/museum")
    Call<MuseumJson> addMuseum(@Header("Authorization") String bearerToken,
                               @Body MuseumJson museum);

    @PATCH("api/museum")
    Call<MuseumJson> updateMuseum(@Header("Authorization") String bearerToken,
                                  @Body MuseumJson museum);

    @GET("api/country")
    Call<RestResponsePage<CountryJson>> getAllCountries(@Query("page") int page,
                                                        @Query("size") int size);

    @GET("api/painting/{id}")
    Call<PaintingResponseJson> getPaintingById(@Path("id") @Nonnull String id);

    @GET("api/painting")
    Call<RestResponsePage<PaintingResponseJson>> getAllPaintings(@Query("page") int page,
                                                                 @Query("size") int size,
                                                                 @Query("title") @Nullable String title);

    @GET("api/painting/author/{authorId}")
    Call<RestResponsePage<PaintingResponseJson>> getPaintingsByAuthorId(@Path("authorId") @Nonnull String authorId,
                                                                        @Query("page") int page,
                                                                        @Query("size") int size);

    @POST("api/painting")
    Call<PaintingResponseJson> addPainting(@Header("Authorization") String bearerToken,
                                           @Body PaintingRequestJson painting);

    @PATCH("api/painting")
    Call<PaintingResponseJson> updatePainting(@Header("Authorization") String bearerToken,
                                              @Body PaintingRequestJson painting);

    @GET("api/session/")
    Call<SessionJson> currentSession(@Header("Authorization") String bearerToken);

    @GET("api/user")
    Call<UserJson> getUser(@Header("Authorization") String bearerToken);

    @PATCH("api/user")
    Call<UserJson> updateUser(@Header("Authorization") String bearerToken,
                              @Body UserJson user);
}