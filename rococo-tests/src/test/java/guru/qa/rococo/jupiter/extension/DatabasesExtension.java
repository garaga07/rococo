package guru.qa.rococo.jupiter.extension;

import guru.qa.rococo.config.Config;
import guru.qa.rococo.data.jdbc.Connections;
import guru.qa.rococo.data.jdbc.DataSources;
import guru.qa.rococo.data.jpa.EntityManagers;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.jdbc.core.JdbcTemplate;

public class DatabasesExtension implements SuiteExtension {
    private static final Config CFG = Config.getInstance();
    private final JdbcTemplate authJdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.authJdbcUrl()));
    private final JdbcTemplate userdataJdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.userdataJdbcUrl()));
    private final JdbcTemplate artistJdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.artistJdbcUrl()));
    private final JdbcTemplate museumJdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.museumJdbcUrl()));
    private final JdbcTemplate paintingJdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.paintingJdbcUrl()));

    @Override
    public void beforeSuite(ExtensionContext context) {
        // Очищаем все таблицы перед началом тестов
        clearDatabaseTables();
    }

    @Override
    public void afterSuite() {
        Connections.closeAllConnections();
        EntityManagers.closeAllEmfs();
    }

    private void clearDatabaseTables() {
        artistJdbcTemplate.execute("TRUNCATE TABLE artist CASCADE;");
        userdataJdbcTemplate.execute("TRUNCATE TABLE \"user\" CASCADE;");
        authJdbcTemplate.execute("TRUNCATE TABLE authority, \"user\" CASCADE;");
        museumJdbcTemplate.execute("TRUNCATE TABLE museum, geo CASCADE;");
        paintingJdbcTemplate.execute("TRUNCATE TABLE painting CASCADE;");
    }
}