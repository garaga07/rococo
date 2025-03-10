package guru.qa.rococo.utils;

import com.github.javafaker.Faker;
import guru.qa.rococo.config.Config;
import guru.qa.rococo.data.entity.museum.CountryEntity;
import guru.qa.rococo.model.rest.CountryJson;
import guru.qa.rococo.model.rest.GeoJson;
import jakarta.persistence.EntityManager;
import org.apache.hc.client5.http.utils.Base64;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import static guru.qa.rococo.data.jpa.EntityManagers.em;

public class RandomDataUtils {

    private static final Faker faker = new Faker();
    private static final Random random = new Random();
    private static final EntityManager entityManager = em(Config.getInstance().museumJdbcUrl());

    @Nonnull
    public static String randomUsername() {
        return faker.name().username();
    }

    @Nonnull
    public static String randomUsername(int length) {
        return generateRandomLatinAlphanumericString(length);
    }

    @Nonnull
    public static String randomLastname() {
        return faker.name().lastName();
    }

    @Nonnull
    public static String randomFirstname() {
        return faker.name().firstName();
    }

    @Nonnull
    public static String randomPassword() {
        return faker.internet().password();
    }

    @Nonnull
    public static String randomPassword(int min, int max) {
        return faker.internet().password(min, max);
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
    public static String randomMuseumTitle() {
        return faker.company().name() + " Museum";
    }

    @Nonnull
    public static String randomMuseumTitle(int length) {
        return randomString(length);
    }

    @Nonnull
    public static String randomPaintingTitle() {
        return faker.book().title() + " Painting";
    }

    @Nonnull
    public static String randomPaintingTitle(int length) {
        return randomString(length);
    }

    @Nonnull
    public static String randomDescription() {
        return faker.lorem().paragraph(5);
    }

    @Nonnull
    public static String randomDescription(int length) {
        return randomString(length);
    }

    @Nonnull
    public static GeoJson randomGeoJson() {
        return new GeoJson(
                randomCity(),
                randomCountryJson()
        );
    }

    @Nonnull
    public static String randomCity() {
        return faker.address().city();
    }

    @Nonnull
    public static String randomCity(int length) {
        return randomString(length);
    }

    @Nonnull
    public static CountryJson randomCountryJson() {
        List<CountryJson> countries = fetchCountriesFromDatabase();
        if (countries.isEmpty()) {
            throw new IllegalStateException("No countries found in the database");
        }
        return countries.get(random.nextInt(countries.size()));
    }

    @Nonnull
    private static List<CountryJson> fetchCountriesFromDatabase() {
        return entityManager.createQuery("SELECT c FROM CountryEntity c", CountryEntity.class)
                .getResultList()
                .stream()
                .map(c -> new CountryJson(c.getId(), c.getName()))
                .collect(Collectors.toList());
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

    @Nonnull
    private static String generateRandomLatinAlphanumericString(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz" +
                "0123456789";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }
}
