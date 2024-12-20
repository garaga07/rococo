package guru.qa.rococo.service;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;

@Service
public class GeoService {
    private static final String COUNTRY_JSON_PATH = "response/get_all_country.json";

    public String getAllCountryJson() throws IOException {
        var resource = new ClassPathResource(COUNTRY_JSON_PATH);
        return Files.readString(resource.getFile().toPath());
    }
}