package guru.qa.rococo.jupiter.extension;

import io.qameta.allure.Allure;
import io.qameta.allure.AllureLifecycle;
import io.qameta.allure.model.TestResult;
import lombok.SneakyThrows;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

public class AllureBackendLogsExtension implements SuiteExtension {
    public static final String caseName = "Rococo backend logs";

    @SneakyThrows
    @Override
    public void afterSuite() {
        String baseDir = getBaseDir();
        System.out.println("Current working directory: " + System.getProperty("user.dir"));
        System.out.println("Base logs directory: " + baseDir);

        final AllureLifecycle allureLifecycle = Allure.getLifecycle();
        final String caseId = UUID.randomUUID().toString();
        allureLifecycle.scheduleTestCase(new TestResult().setUuid(caseId).setName(caseName));
        allureLifecycle.startTestCase(caseId);

        addLogAttachment("Rococo-auth log", baseDir, "logs/rococo-auth/app.log");
        addLogAttachment("Rococo-gateway log", baseDir, "logs/rococo-gateway/app.log");
        addLogAttachment("Rococo-userdata log", baseDir, "logs/rococo-userdata/app.log");
        addLogAttachment("Rococo-artist log", baseDir, "logs/rococo-artist/app.log");
        addLogAttachment("Rococo-museum log", baseDir, "logs/rococo-museum/app.log");
        addLogAttachment("Rococo-painting log", baseDir, "logs/rococo-painting/app.log");

        allureLifecycle.stopTestCase(caseId);
        allureLifecycle.writeTestCase(caseId);
    }

    @SneakyThrows
    private void addLogAttachment(String attachmentName, String baseDir, String relativeLogPath) {
        Path logPath = Paths.get(baseDir, relativeLogPath);
        if (!Files.exists(logPath)) {
            System.out.println("Log file not found: " + logPath);
            return;
        }
        Allure.getLifecycle().addAttachment(
                attachmentName,
                "text/plain",
                ".log",
                Files.newInputStream(logPath)
        );
    }

    private String getBaseDir() {
        Path currentDir = Paths.get(System.getProperty("user.dir"));
        // Если запущено из Gradle, вернем родительскую папку (выход из rococo-tests)
        if (currentDir.endsWith("rococo-tests")) {
            return currentDir.getParent().toString();
        }
        return currentDir.toString();
    }
}