package epam.task.gymbootdb.exception;

import lombok.Getter;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Getter
public abstract class GymResponseStatusException extends ResponseStatusException {

    private final String transactionId;
    private final String errorId;

    protected GymResponseStatusException(HttpStatus status, String message) {
        super(status, message);
        transactionId = MDC.get("transactionId");
        errorId = UUID.randomUUID().toString();
    }
}
