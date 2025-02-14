package guru.qa.rococo.utils;

import com.github.javafaker.Faker;
import org.apache.hc.client5.http.utils.Base64;

import javax.annotation.Nonnull;
import java.nio.charset.StandardCharsets;
import java.util.Random;

public class RandomDataUtils {

    private static final Faker faker = new Faker();
    private static final Random random = new Random();

    @Nonnull
    public static String randomUsername() {
        return faker.name().username();
    }

    @Nonnull
    public static String randomArtistName() {
        return faker.artist().name();
    }

    @Nonnull
    public static String randomArtistName(int length) {
        return randomString(length);
    }

    @Nonnull
    public static String randomBiography() {
        return faker.lorem().paragraph(3);
    }

    @Nonnull
    public static String randomBiography(int length) {
        return randomString(length);
    }

    @Nonnull
    public static String randomBase64Image() {
        byte[] randomBytes = new byte[256];
        new Random().nextBytes(randomBytes);
        String base64 = Base64.encodeBase64String(randomBytes);
        return "data:image/jpeg;base64," + base64;
    }

    @Nonnull
    public static String randomBase64Image(int number) {
        byte[] randomBytes = new byte[number];
        new Random().nextBytes(randomBytes);
        String base64 = Base64.encodeBase64String(randomBytes);
        return "data:image/jpeg;base64," + base64;
    }

    @Nonnull
    private static String randomString(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz" +
                "АБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ" +
                "абвгдеёжзийклмнопрстуфхцчшщъыьэюя" +
                "0123456789";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }
}
