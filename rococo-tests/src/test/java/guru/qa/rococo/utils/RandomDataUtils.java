package guru.qa.rococo.utils;

import com.github.javafaker.Faker;
import guru.qa.rococo.model.rest.CountryJson;
import guru.qa.rococo.model.rest.GeoJson;
import org.apache.hc.client5.http.utils.Base64;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Random;
import java.util.UUID;

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
    public static String randomMuseumTitle() {
        return faker.company().name() + " Museum";
    }

    @Nonnull
    public static String randomMuseumTitle(int length) {
        return randomString(length);
    }

    @Nonnull
    public static String randomMuseumDescription() {
        return faker.lorem().paragraph(5);
    }

    @Nonnull
    public static String randomMuseumDescription(int length) {
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
        return COUNTRIES.get(random.nextInt(COUNTRIES.size()));
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

    private static final List<CountryJson> COUNTRIES = List.of(
            new CountryJson(UUID.fromString("3fd934d4-ea16-11ef-9bc9-0242ac110002"), "Австралия"),
            new CountryJson(UUID.fromString("3fd93a56-ea16-11ef-9bc9-0242ac110002"), "Австрия"),
            new CountryJson(UUID.fromString("3fd93aba-ea16-11ef-9bc9-0242ac110002"), "Азербайджан"),
            new CountryJson(UUID.fromString("3fd93af6-ea16-11ef-9bc9-0242ac110002"), "Албания"),
            new CountryJson(UUID.fromString("3fd93b32-ea16-11ef-9bc9-0242ac110002"), "Алжир"),
            new CountryJson(UUID.fromString("3fd93b64-ea16-11ef-9bc9-0242ac110002"), "Ангола"),
            new CountryJson(UUID.fromString("3fd93ba0-ea16-11ef-9bc9-0242ac110002"), "Андорра"),
            new CountryJson(UUID.fromString("3fd93bd2-ea16-11ef-9bc9-0242ac110002"), "Антигуа и Барбуда"),
            new CountryJson(UUID.fromString("3fd93c0e-ea16-11ef-9bc9-0242ac110002"), "Аргентина"),
            new CountryJson(UUID.fromString("3fd93c4a-ea16-11ef-9bc9-0242ac110002"), "Армения"),
            new CountryJson(UUID.fromString("3fd93c7c-ea16-11ef-9bc9-0242ac110002"), "Афганистан"),
            new CountryJson(UUID.fromString("3fd93cb8-ea16-11ef-9bc9-0242ac110002"), "Багамские Острова"),
            new CountryJson(UUID.fromString("3fd93cea-ea16-11ef-9bc9-0242ac110002"), "Бангладеш"),
            new CountryJson(UUID.fromString("3fd93d1c-ea16-11ef-9bc9-0242ac110002"), "Барбадос"),
            new CountryJson(UUID.fromString("3fd93d58-ea16-11ef-9bc9-0242ac110002"), "Бахрейн"),
            new CountryJson(UUID.fromString("3fd93db2-ea16-11ef-9bc9-0242ac110002"), "Белиз"),
            new CountryJson(UUID.fromString("3fd93de4-ea16-11ef-9bc9-0242ac110002"), "Белоруссия"),
            new CountryJson(UUID.fromString("3fd93e16-ea16-11ef-9bc9-0242ac110002"), "Бельгия"),
            new CountryJson(UUID.fromString("3fd93e52-ea16-11ef-9bc9-0242ac110002"), "Бенин"),
            new CountryJson(UUID.fromString("3fd93e84-ea16-11ef-9bc9-0242ac110002"), "Болгария"),
            new CountryJson(UUID.fromString("3fd93eb6-ea16-11ef-9bc9-0242ac110002"), "Боливия"),
            new CountryJson(UUID.fromString("3fd93ef2-ea16-11ef-9bc9-0242ac110002"), "Босния и Герцеговина"),
            new CountryJson(UUID.fromString("3fd93f2e-ea16-11ef-9bc9-0242ac110002"), "Ботсвана"),
            new CountryJson(UUID.fromString("3fd93fec-ea16-11ef-9bc9-0242ac110002"), "Бразилия"),
            new CountryJson(UUID.fromString("3fd94064-ea16-11ef-9bc9-0242ac110002"), "Бруней"),
            new CountryJson(UUID.fromString("3fd940a0-ea16-11ef-9bc9-0242ac110002"), "Буркина-Фасо"),
            new CountryJson(UUID.fromString("3fd940dc-ea16-11ef-9bc9-0242ac110002"), "Бурунди"),
            new CountryJson(UUID.fromString("3fd9410e-ea16-11ef-9bc9-0242ac110002"), "Бутан"),
            new CountryJson(UUID.fromString("3fd9414a-ea16-11ef-9bc9-0242ac110002"), "Вануату"),
            new CountryJson(UUID.fromString("3fd9417c-ea16-11ef-9bc9-0242ac110002"), "Ватикан"),
            new CountryJson(UUID.fromString("3fd941ae-ea16-11ef-9bc9-0242ac110002"), "Великобритания"),
            new CountryJson(UUID.fromString("3fd941f4-ea16-11ef-9bc9-0242ac110002"), "Венгрия"),
            new CountryJson(UUID.fromString("3fd94226-ea16-11ef-9bc9-0242ac110002"), "Венесуэла"),
            new CountryJson(UUID.fromString("3fd94258-ea16-11ef-9bc9-0242ac110002"), "Восточный Тимор"),
            new CountryJson(UUID.fromString("3fd94294-ea16-11ef-9bc9-0242ac110002"), "Вьетнам"),
            new CountryJson(UUID.fromString("3fd942d0-ea16-11ef-9bc9-0242ac110002"), "Габон"),
            new CountryJson(UUID.fromString("3fd94302-ea16-11ef-9bc9-0242ac110002"), "Гайана"),
            new CountryJson(UUID.fromString("3fd94334-ea16-11ef-9bc9-0242ac110002"), "Гамбия"),
            new CountryJson(UUID.fromString("3fd94366-ea16-11ef-9bc9-0242ac110002"), "Гана"),
            new CountryJson(UUID.fromString("3fd943a2-ea16-11ef-9bc9-0242ac110002"), "Гватемала"),
            new CountryJson(UUID.fromString("3fd943f2-ea16-11ef-9bc9-0242ac110002"), "Гвинея"),
            new CountryJson(UUID.fromString("3fd9442e-ea16-11ef-9bc9-0242ac110002"), "Гвинея-Бисау"),
            new CountryJson(UUID.fromString("3fd94460-ea16-11ef-9bc9-0242ac110002"), "Германия"),
            new CountryJson(UUID.fromString("3fd9449c-ea16-11ef-9bc9-0242ac110002"), "Гондурас"),
            new CountryJson(UUID.fromString("3fd944d8-ea16-11ef-9bc9-0242ac110002"), "Гренада"),
            new CountryJson(UUID.fromString("3fd9450a-ea16-11ef-9bc9-0242ac110002"), "Греция"),
            new CountryJson(UUID.fromString("3fd9453c-ea16-11ef-9bc9-0242ac110002"), "Грузия"),
            new CountryJson(UUID.fromString("3fd9456e-ea16-11ef-9bc9-0242ac110002"), "Дания"),
            new CountryJson(UUID.fromString("3fd945aa-ea16-11ef-9bc9-0242ac110002"), "Демократическая Республика Конго"),
            new CountryJson(UUID.fromString("3fd945dc-ea16-11ef-9bc9-0242ac110002"), "Джибути"),
            new CountryJson(UUID.fromString("3fd94618-ea16-11ef-9bc9-0242ac110002"), "Доминика"),
            new CountryJson(UUID.fromString("3fd94654-ea16-11ef-9bc9-0242ac110002"), "Доминиканская Республика"),
            new CountryJson(UUID.fromString("3fd94690-ea16-11ef-9bc9-0242ac110002"), "Египет"),
            new CountryJson(UUID.fromString("3fd946cc-ea16-11ef-9bc9-0242ac110002"), "Замбия"),
            new CountryJson(UUID.fromString("3fd94712-ea16-11ef-9bc9-0242ac110002"), "Зимбабве"),
            new CountryJson(UUID.fromString("3fd94744-ea16-11ef-9bc9-0242ac110002"), "Израиль"),
            new CountryJson(UUID.fromString("3fd94780-ea16-11ef-9bc9-0242ac110002"), "Индия"),
            new CountryJson(UUID.fromString("3fd947bc-ea16-11ef-9bc9-0242ac110002"), "Индонезия"),
            new CountryJson(UUID.fromString("3fd947f8-ea16-11ef-9bc9-0242ac110002"), "Иордания"),
            new CountryJson(UUID.fromString("3fd94834-ea16-11ef-9bc9-0242ac110002"), "Ирак"),
            new CountryJson(UUID.fromString("3fd94866-ea16-11ef-9bc9-0242ac110002"), "Иран"),
            new CountryJson(UUID.fromString("3fd948ac-ea16-11ef-9bc9-0242ac110002"), "Ирландия"),
            new CountryJson(UUID.fromString("3fd948e8-ea16-11ef-9bc9-0242ac110002"), "Исландия"),
            new CountryJson(UUID.fromString("3fd94924-ea16-11ef-9bc9-0242ac110002"), "Испания"),
            new CountryJson(UUID.fromString("3fd94960-ea16-11ef-9bc9-0242ac110002"), "Италия"),
            new CountryJson(UUID.fromString("3fd949ba-ea16-11ef-9bc9-0242ac110002"), "Йемен"),
            new CountryJson(UUID.fromString("3fd949f6-ea16-11ef-9bc9-0242ac110002"), "Кабо-Верде"),
            new CountryJson(UUID.fromString("3fd94a32-ea16-11ef-9bc9-0242ac110002"), "Казахстан"),
            new CountryJson(UUID.fromString("3fd94a78-ea16-11ef-9bc9-0242ac110002"), "Камбоджа"),
            new CountryJson(UUID.fromString("3fd94abe-ea16-11ef-9bc9-0242ac110002"), "Камерун"),
            new CountryJson(UUID.fromString("3fd94afa-ea16-11ef-9bc9-0242ac110002"), "Канада"),
            new CountryJson(UUID.fromString("3fd94b36-ea16-11ef-9bc9-0242ac110002"), "Катар"),
            new CountryJson(UUID.fromString("3fd94b72-ea16-11ef-9bc9-0242ac110002"), "Кения"),
            new CountryJson(UUID.fromString("3fd94ba4-ea16-11ef-9bc9-0242ac110002"), "Киргизия"),
            new CountryJson(UUID.fromString("3fd94bea-ea16-11ef-9bc9-0242ac110002"), "Кирибати"),
            new CountryJson(UUID.fromString("3fd94c26-ea16-11ef-9bc9-0242ac110002"), "Китай"),
            new CountryJson(UUID.fromString("3fd94c58-ea16-11ef-9bc9-0242ac110002"), "Колумбия"),
            new CountryJson(UUID.fromString("3fd94c94-ea16-11ef-9bc9-0242ac110002"), "Коморы"),
            new CountryJson(UUID.fromString("3fd94cd0-ea16-11ef-9bc9-0242ac110002"), "Корейская Народно-Демократическая Республика"),
            new CountryJson(UUID.fromString("3fd94d02-ea16-11ef-9bc9-0242ac110002"), "Коста-Рика"),
            new CountryJson(UUID.fromString("3fd94d48-ea16-11ef-9bc9-0242ac110002"), "Кот-д’Ивуар"),
            new CountryJson(UUID.fromString("3fd94d84-ea16-11ef-9bc9-0242ac110002"), "Куба"),
            new CountryJson(UUID.fromString("3fd94dc0-ea16-11ef-9bc9-0242ac110002"), "Кувейт"),
            new CountryJson(UUID.fromString("3fd94dfc-ea16-11ef-9bc9-0242ac110002"), "Лаос"),
            new CountryJson(UUID.fromString("3fd94e38-ea16-11ef-9bc9-0242ac110002"), "Латвия"),
            new CountryJson(UUID.fromString("3fd94f1e-ea16-11ef-9bc9-0242ac110002"), "Лесото"),
            new CountryJson(UUID.fromString("3fd94faa-ea16-11ef-9bc9-0242ac110002"), "Либерия"),
            new CountryJson(UUID.fromString("3fd94ff0-ea16-11ef-9bc9-0242ac110002"), "Ливан"),
            new CountryJson(UUID.fromString("3fd9502c-ea16-11ef-9bc9-0242ac110002"), "Ливия"),
            new CountryJson(UUID.fromString("3fd9505e-ea16-11ef-9bc9-0242ac110002"), "Литва"),
            new CountryJson(UUID.fromString("3fd950b8-ea16-11ef-9bc9-0242ac110002"), "Лихтенштейн"),
            new CountryJson(UUID.fromString("3fd950fe-ea16-11ef-9bc9-0242ac110002"), "Люксембург"),
            new CountryJson(UUID.fromString("3fd95144-ea16-11ef-9bc9-0242ac110002"), "Маврикий"),
            new CountryJson(UUID.fromString("3fd9518a-ea16-11ef-9bc9-0242ac110002"), "Мавритания"),
            new CountryJson(UUID.fromString("3fd951bc-ea16-11ef-9bc9-0242ac110002"), "Мадагаскар"),
            new CountryJson(UUID.fromString("3fd951f8-ea16-11ef-9bc9-0242ac110002"), "Малави"),
            new CountryJson(UUID.fromString("3fd9523e-ea16-11ef-9bc9-0242ac110002"), "Малайзия"),
            new CountryJson(UUID.fromString("3fd95270-ea16-11ef-9bc9-0242ac110002"), "Мали"),
            new CountryJson(UUID.fromString("3fd952a2-ea16-11ef-9bc9-0242ac110002"), "Мальдивы"),
            new CountryJson(UUID.fromString("3fd96fb2-ea16-11ef-9bc9-0242ac110002"), "Япония")
    );
}
