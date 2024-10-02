package epam.task.gymbootdb.monitor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class DbHealthIndicator implements HealthIndicator {

    private final String db;

    private final JdbcTemplate jdbcTemplate;

    public DbHealthIndicator(@Value("${db}") String db, JdbcTemplate jdbcTemplate) {
        this.db = db;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Health health() {
        try {
            long responseTime = getResponseTime();
            return Health.up()
                    .withDetail("database", db)
                    .withDetail("responseTime", responseTime + "ms")
                    .build();
        } catch (Exception e) {
            return Health.outOfService()
                    .withDetail("database",  db + " database is down")
                    .withDetail("error", e.getMessage())
                    .build();
        }
    }

    private long getResponseTime() {
        long startTime = System.currentTimeMillis();
        jdbcTemplate.execute("SELECT 1");
        return System.currentTimeMillis() - startTime;
    }
}
