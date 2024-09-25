package epam.task.gymbootdb.monitor;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DbHealthIndicator implements HealthIndicator {

    @Value("${db}")
    private String db;

    private final JdbcTemplate jdbcTemplate;

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
