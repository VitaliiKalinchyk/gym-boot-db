package epam.task.gymbootdb.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class UnauthorizedAuthenticationEntryPoint implements AuthenticationEntryPoint {

    public static final String PLEASE_PROVIDE_A_VALID_JWT_TOKEN =
            "{ \"error\": \"You are not authenticated. Please provide a valid JWT token.\" }";

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        log.warn("Unauthorized access attempt: {}", request.getRequestURI());

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(PLEASE_PROVIDE_A_VALID_JWT_TOKEN);
    }
}
