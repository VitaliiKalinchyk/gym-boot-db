package epam.task.gymbootdb.interceptor;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.MDC;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RestLoggingInterceptorTest {

    public static final String TRANSACTION_ID = "transactionId";
    public static final String ID = "12345";
    public static final String GET = "GET";
    public static final String URI = "/api/test";
    public static final String PARAM_VALUE = "param=value";
    public static final String LOG_REQUEST = "Transaction ID: {}, HTTP Method: {}, Endpoint: {}{}";
    public static final String LOG_RESPONSE = "Transaction ID: {}, Response Status: {}, Message: {}";
    public static final String RESPONSE_OK = "Response OK";
    public static final String INTERNAL_SERVER_ERROR = "Internal server error";

    RestLoggingInterceptor interceptor;

    @Mock
    private Logger log;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;

    @BeforeEach
    public void setup() {
        interceptor = new RestLoggingInterceptor();
        interceptor.setLog(log);
        MDC.clear();
    }

    @AfterEach
    public void setOff() {
        MDC.clear();
    }

    @Test
    void testPreHandleWithQueryString() {
        MDC.put(TRANSACTION_ID, ID);
        when(request.getMethod()).thenReturn(GET);
        when(request.getRequestURI()).thenReturn(URI);
        when(request.getQueryString()).thenReturn(PARAM_VALUE);

        boolean b = interceptor.preHandle(request, response, new Object());

        assertTrue(b);
        verifyPreHandle("?" + PARAM_VALUE);
    }

    @Test
    void testPreHandleWithNullQueryString() {
        MDC.put(TRANSACTION_ID, ID);

        when(request.getMethod()).thenReturn(GET);
        when(request.getRequestURI()).thenReturn(URI);

        boolean b = interceptor.preHandle(request, response, new Object());

        assertTrue(b);
        verifyPreHandle("");
    }

    @Test
    void testAfterCompletion() {
        MDC.put(TRANSACTION_ID, ID);

        when(response.getStatus()).thenReturn(200);

        interceptor.afterCompletion(request, response, new Object(), null);

        verifyAfterCompletion(200, RESPONSE_OK);
    }

    @Test
    void testAfterCompletionWithError() {
        MDC.put(TRANSACTION_ID, ID);
        Exception exception = new Exception(INTERNAL_SERVER_ERROR);

        when(response.getStatus()).thenReturn(500);

        interceptor.afterCompletion(request, response, new Object(), exception);

        verifyAfterCompletion(500, INTERNAL_SERVER_ERROR);
    }

    private void verifyPreHandle(String PARAM_VALUE) {
        verify(log).info(
                eq(LOG_REQUEST),
                eq(ID),
                eq(GET),
                eq(URI),
                eq(PARAM_VALUE)
        );
    }

    private void verifyAfterCompletion(int value, String responseOk) {
        verify(log).info(
                eq(LOG_RESPONSE),
                eq(ID),
                eq(value),
                eq(responseOk)
        );
    }
}