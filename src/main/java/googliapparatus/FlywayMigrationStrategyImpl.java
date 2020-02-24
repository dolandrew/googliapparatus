package googliapparatus;

import org.flywaydb.core.Flyway;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.boot.autoconfigure.flyway.FlywayProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import static java.util.Collections.singletonList;

@Component
public class FlywayMigrationStrategyImpl implements FlywayMigrationStrategy {

    @Override
    public void migrate(Flyway flyway) {
        flyway.migrate();
    }

    @Bean
    @Primary
    private FlywayProperties flywayProperties() {
        FlywayProperties flywayProperties = new FlywayProperties();
        flywayProperties.setLocations(singletonList("classpath:migration"));
        flywayProperties.setOutOfOrder(true);
        return flywayProperties;
    }
}
