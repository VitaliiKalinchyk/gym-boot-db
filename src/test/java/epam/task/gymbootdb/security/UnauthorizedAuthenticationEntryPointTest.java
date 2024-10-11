package epam.task.gymbootdb.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;

import java.io.PrintWriter;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UnauthorizedAuthenticationEntryPointTest {

    @InjectMocks
    private UnauthorizedAuthenticationEntryPoint entryPoint;

    @Mock
    HttpServletRequest request;
    @Mock
    HttpServletResponse response;
    @Mock
    AuthenticationException authException;
    @Mock
    PrintWriter writer;

    @Test
    void commence() throws Exception {
        when(response.getWriter()).thenReturn(writer);

        entryPoint.commence(request, response, authException);

        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(response).setContentType(MediaType.APPLICATION_JSON_VALUE);
        verify(writer).write(UnauthorizedAuthenticationEntryPoint.PLEASE_PROVIDE_A_VALID_JWT_TOKEN);
    }
}
