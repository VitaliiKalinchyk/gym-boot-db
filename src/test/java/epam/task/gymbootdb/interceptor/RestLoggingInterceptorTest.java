package epam.task.gymbootdb.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RestLoggingInterceptorTest {

    private static final String GET = "GET";
    private static final String URI = "/api/test";
    private static final String PARAM_VALUE = "param=value";

    @InjectMocks
    RestLoggingInterceptor interceptor;

    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;

    @Test
    void preHandleWithQueryString() {
        when(request.getMethod()).thenReturn(GET);
        when(request.getRequestURI()).thenReturn(URI);
        when(request.getQueryString()).thenReturn(PARAM_VALUE);

        boolean b = interceptor.preHandle(request, response, new Object());

        assertTrue(b);
    }

    @Test
    void preHandleWithNullQueryString() {
        when(request.getMethod()).thenReturn(GET);
        when(request.getRequestURI()).thenReturn(URI);

        boolean b = interceptor.preHandle(request, response, new Object());

        assertTrue(b);
    }

    @Test
    void afterCompletion() {
        when(response.getStatus()).thenReturn(200);

        assertDoesNotThrow(() -> interceptor.afterCompletion(request, response, new Object(), null));
    }
}
