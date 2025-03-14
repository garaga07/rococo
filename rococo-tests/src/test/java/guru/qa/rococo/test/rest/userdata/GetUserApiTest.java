//package guru.qa.rococo.test.rest.userdata;
//
//import guru.qa.rococo.jupiter.annotation.ApiLogin;
//import guru.qa.rococo.jupiter.annotation.Token;
//import guru.qa.rococo.jupiter.annotation.User;
//import guru.qa.rococo.jupiter.annotation.meta.RestTest;
//import guru.qa.rococo.jupiter.extension.ApiLoginExtension;
//import guru.qa.rococo.jupiter.extension.UserExtension;
//import guru.qa.rococo.model.rest.UserJson;
//import guru.qa.rococo.service.impl.GatewayApiClient;
//import io.qameta.allure.Feature;
//import io.qameta.allure.Severity;
//import io.qameta.allure.SeverityLevel;
//import io.qameta.allure.Story;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Tag;
//import org.junit.jupiter.api.Tags;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.RegisterExtension;
//import retrofit2.Response;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//@RestTest
//@DisplayName("GetUserApi")
//public class GetUserApiTest {
//    @RegisterExtension
//    static final UserExtension userExtension = new UserExtension();
//    @RegisterExtension
//    private static final ApiLoginExtension apiLoginExtension = ApiLoginExtension.rest();
//    private final GatewayApiClient gatewayApiClient = new GatewayApiClient();
//
//    @User
//    @ApiLogin
//    @Story("Пользователи")
//    @Severity(SeverityLevel.BLOCKER)
//    @Feature("Получение информации о текущем пользователе")
//    @Tags({@Tag("api")})
//    @Test
//    @DisplayName("API: Успешное получение информации о текущем пользователе")
//    void shouldSuccessfullyGetCurrentUserInfo(@Token String token, UserJson user) {
//        Response<UserJson> response = gatewayApiClient.getUser(token);
//        assertEquals(200, response.code(), "Expected HTTP status 200 but got " + response.code());
//        UserJson responseBody = response.body();
//        assertNotNull(responseBody, "Response body should not be null");
//        assertAll(
//                () -> assertEquals(user.id(), responseBody.id(),
//                        String.format("User id mismatch! Expected: '%s', Actual: '%s'", user.id(), responseBody.id())),
//                () -> assertEquals(user.username(), responseBody.username(),
//                        String.format("User name mismatch! Expected: '%s', Actual: '%s'", user.username(), responseBody.username())),
//                () -> assertEquals(user.firstname(), responseBody.firstname(),
//                        String.format("User firstname mismatch! Expected: '%s', Actual: '%s'", user.firstname(), responseBody.firstname())),
//                () -> assertEquals(user.lastname(), responseBody.lastname(),
//                        String.format("User lastname mismatch! Expected: '%s', Actual: '%s'", user.lastname(), responseBody.lastname())),
//                () -> assertEquals(user.avatar(), responseBody.avatar(),
//                        String.format("User avatar mismatch! Expected: '%s', Actual: '%s'", user.avatar(), responseBody.avatar()))
//        );
//    }
//
//    @Story("Пользователи")
//    @Severity(SeverityLevel.CRITICAL)
//    @Feature("Получение информации о текущем пользователе")
//    @Tags({@Tag("api")})
//    @Test
//    @DisplayName("API: Ошибка 401 при попытке получить информацию о пользователе без авторизации")
//    void shouldFailToGetUserInfoWithoutAuthorization() {
//        Response<UserJson> response = gatewayApiClient.getUser("invalid_token");
//        assertEquals(401, response.code(), "Expected HTTP status 401 but got " + response.code());
//    }
//}