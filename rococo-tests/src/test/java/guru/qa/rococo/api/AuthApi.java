package guru.qa.rococo.api;

import retrofit2.Call;
import retrofit2.http.*;

public interface AuthApi {

    @GET("oauth2/authorize")
    Call<Void> authorize(
            @Query("response_type") String responseType,
            @Query("client_id") String clientId,
            @Query("scope") String scope,
            @Query(value = "redirect_uri", encoded = true) String redirectUri,
            @Query("code_challenge") String codeChallenge,
            @Query("code_challenge_method") String codeChallengeMethod);

    @POST("oauth2/token")
    @FormUrlEncoded
    Call<String> token(
            @Field("client_id") String clientId,
            @Field(value = "redirect_uri", encoded = true) String redirectUri,
            @Field("grant_type") String grantType,
            @Field("code") String code,
            @Field("code_verifier") String codeChallenge);

    @POST("login")
    @FormUrlEncoded
    Call<String> login(@Field("username") String username,
                     @Field("password") String password,
                     @Field("_csrf") String csrf
    );

    @GET("register")
    Call<Void> requestRegisterForm();

    @POST("register")
    @FormUrlEncoded
    Call<Void> register(
            @Field("username") String username,
            @Field("password") String password,
            @Field("passwordSubmit") String passwordSubmit,
            @Field("_csrf") String csrf);
}