package epam.task.gymbootdb.handler;

import epam.task.gymbootdb.exception.PasswordException;
import epam.task.gymbootdb.exception.TraineeException;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class GymExceptionHandlerTest {

    private static final GymExceptionHandler handler = new GymExceptionHandler();
    public static final String TRANSACTION_ID = "timestamp";
    public static final String MESSAGE = "message";

    @Test
    public void testHandleNotFoundException() {
        ResponseEntity<Map<String, Object>> responseEntity = handler.handleNotFoundException(new TraineeException(1));

        asserResponseEntity(responseEntity, HttpStatus.NOT_FOUND, "Trainee with id 1 was not found");
    }

    @Test
    public void testHandlePasswordException() {
        ResponseEntity<Map<String, Object>> responseEntity = handler.handlePasswordException(new PasswordException());

        asserResponseEntity(responseEntity, HttpStatus.UNAUTHORIZED, "Wrong password");
    }

    @Test
    public void testHandleValidationException() {
        FieldError fieldError = new FieldError("objectName", "fieldName", "Error message");
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        Mockito.when(exception.getFieldErrors()).thenReturn(Collections.singletonList(fieldError));

        ResponseEntity<Map<String, Object>> responseEntity = handler.handleValidationException(exception);
        Map<String, Object> body = responseEntity.getBody();

        assertNotNull(body);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertTrue(body.containsKey(TRANSACTION_ID));
        assertTrue(body.containsKey("fieldName"));
        assertTrue(body.containsValue(Collections.singletonList("Error message")));
    }

    @Test
    public void testHandleGlobalException() {
        ResponseEntity<Map<String, Object>> responseEntity = handler.handleGlobalException(new Exception());

        asserResponseEntity(responseEntity, HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error occurred");
    }

    private static void asserResponseEntity(ResponseEntity<Map<String, Object>> responseEntity,
                                            HttpStatus status, String message) {
        Map<String, Object> body = responseEntity.getBody();

        assertNotNull(body);
        assertEquals(status, responseEntity.getStatusCode());
        assertTrue(body.containsKey(TRANSACTION_ID));
        assertTrue(body.containsKey(MESSAGE));
        assertTrue(body.containsValue(message));
    }
}