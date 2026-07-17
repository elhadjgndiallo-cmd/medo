package com.medo.api.config;

import org.flywaydb.core.Flyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import javax.sql.DataSource;

@Configuration
public class FlywayConfig {

    private static final Logger log = LoggerFactory.getLogger(FlywayConfig.class);

    @Autowired
    private DataSource dataSource;

    @Value("${spring.flyway.locations:classpath:db/migration/public}")
    private String location;

    @Bean(name = "flywayPublic", initMethod = "migrate")
    public Flyway flywayPublic() {
        log.info("Flyway migrations schéma public : {}", location);
        return Flyway.configure()
            .dataSource(dataSource)
            .schemas("public")
            .locations(location)
            .baselineOnMigrate(true)
            .baselineVersion("0")
            .validateOnMigrate(true)
            .table("flyway_schema_history_public")
            .load();
    }
}
