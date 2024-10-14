package epam.task.gymbootdb.handler;

import epam.task.gymbootdb.exception.TraineeException;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class GymExceptionHandlerTest {

    public static final String TIMESTAMP = "timestamp";
    public static final String MESSAGE = "message";
    public static final String FIELD_NAME = "fieldName";
    public static final String ERROR_MESSAGE = "Error message";
    public static final String ERROR_ID = "errorId";

    private final GymExceptionHandler handler = new GymExceptionHandler();

    @Test
    void testGymResponseStatusException() {
        TraineeException e = new TraineeException("username");

        ResponseEntity<Map<String, Object>> responseEntity =
                handler.handleGymResponseStatusException(e);

        asserResponseEntity(responseEntity, e.getStatusCode(), e.getReason());
    }

    @Test
    void handleValidationException() {
        FieldError fieldError = new FieldError("objectName", FIELD_NAME, ERROR_MESSAGE);
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        Mockito.when(exception.getFieldErrors()).thenReturn(Collections.singletonList(fieldError));

        ResponseEntity<Map<String, Object>> responseEntity = handler.handleValidationException(exception);
        Map<String, Object> body = responseEntity.getBody();

        assertNotNull(body);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertTrue(body.containsKey(TIMESTAMP));
        assertTrue(body.containsKey(FIELD_NAME));
        assertTrue(body.containsKey(ERROR_ID));
        assertTrue(body.containsValue(Collections.singletonList(ERROR_MESSAGE)));
    }

    @Test
    void handleGlobalException() {
        ResponseEntity<Map<String, Object>> responseEntity = handler.handleGlobalException(new Exception());

        asserResponseEntity(responseEntity, HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error occurred");
    }

    private void asserResponseEntity(ResponseEntity<Map<String, Object>> responseEntity,
                                            HttpStatusCode status, String message) {
        Map<String, Object> body = responseEntity.getBody();

        assertNotNull(body);
        assertEquals(status, responseEntity.getStatusCode());
        assertTrue(body.containsKey(TIMESTAMP));
        assertTrue(body.containsKey(MESSAGE));
        assertTrue(body.containsValue(message));
    }
}
