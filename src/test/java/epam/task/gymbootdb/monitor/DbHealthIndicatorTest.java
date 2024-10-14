package epam.task.gymbootdb.monitor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DbHealthIndicatorTest {

    private DbHealthIndicator dbHealthIndicator;

    @Mock
    JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setup() {
        dbHealthIndicator = new DbHealthIndicator("H2", jdbcTemplate);
    }

    @Test
    void health(){
        doNothing().when(jdbcTemplate).execute("SELECT 1");

        Health health = dbHealthIndicator.health();
        Map<String, Object> details = health.getDetails();

        assertEquals(Status.UP, health.getStatus());
        assertNotNull(details);
        assertEquals("H2", details.get("database"));
        assertTrue(details.containsKey("responseTime"));
    }
    @Test
    void healthNoConnection(){
        doThrow(new RuntimeException("message")).when(jdbcTemplate).execute("SELECT 1");

        Health health = dbHealthIndicator.health();
        Map<String, Object> details = health.getDetails();

        assertEquals(Status.OUT_OF_SERVICE, health.getStatus());
        assertNotNull(details);
        assertEquals("H2 database is down", details.get("database"));
        assertEquals("message", details.get("error"));
    }
}