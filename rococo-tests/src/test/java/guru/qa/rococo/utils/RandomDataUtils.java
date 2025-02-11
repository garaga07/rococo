package guru.qa.rococo.utils;

import com.github.javafaker.Faker;
import org.apache.hc.client5.http.utils.Base64;

import javax.annotation.Nonnull;
import java.util.Random;

public class RandomDataUtils {

    private static final Faker faker = new Faker();

    @Nonnull
    public static String randomUsername() {
        return faker.name().username();
    }

    @Nonnull
    public static String randomArtistName() {
        return faker.artist().name();
    }

    @Nonnull
    public static String randomBiography() {
        return faker.lorem().paragraph(3);
    }

    @Nonnull
    public static String randomBase64Image() {
        byte[] randomBytes = new byte[256];
        new Random().nextBytes(randomBytes);
        String base64 = Base64.encodeBase64String(randomBytes);
        return "data:image/jpeg;base64," + base64;
    }
}
