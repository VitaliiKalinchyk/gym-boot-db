package epam.task.gymbootdb.interceptor;


import epam.task.gymbootdb.service.LoggingService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RestLoggingInterceptorTest {

    public static final String GET = "GET";
    public static final String URI = "/api/test";
    public static final String PARAM_VALUE = "param=value";
    public static final String RESPONSE_OK = "Response OK";
    public static final String INTERNAL_SERVER_ERROR = "Internal server error";

    @InjectMocks
    RestLoggingInterceptor interceptor;

    @Mock
    private LoggingService loggingService;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;

    @Test
    void testPreHandleWithQueryString() {
        when(request.getMethod()).thenReturn(GET);
        when(request.getRequestURI()).thenReturn(URI);
        when(request.getQueryString()).thenReturn(PARAM_VALUE);

        boolean b = interceptor.preHandle(request, response, new Object());

        assertTrue(b);
        verify(loggingService).logInfoInterceptorPreHandle(GET, URI, "?" + PARAM_VALUE);
    }

    @Test
    void testPreHandleWithNullQueryString() {
        when(request.getMethod()).thenReturn(GET);
        when(request.getRequestURI()).thenReturn(URI);

        boolean b = interceptor.preHandle(request, response, new Object());

        assertTrue(b);
        verify(loggingService).logInfoInterceptorPreHandle(GET, URI, "");
    }

    @Test
    void testAfterCompletion() {
        when(response.getStatus()).thenReturn(200);

        interceptor.afterCompletion(request, response, new Object(), null);

        verify(loggingService).logInfoInterceptorPostHandle(200, RESPONSE_OK);
    }

    @Test
    void testAfterCompletionWithError() {
        Exception exception = new Exception(INTERNAL_SERVER_ERROR);

        when(response.getStatus()).thenReturn(500);

        interceptor.afterCompletion(request, response, new Object(), exception);

        verify(loggingService).logInfoInterceptorPostHandle(500, INTERNAL_SERVER_ERROR);
    }
}
