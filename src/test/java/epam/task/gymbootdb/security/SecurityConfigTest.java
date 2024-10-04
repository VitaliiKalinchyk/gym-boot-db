package epam.task.gymbootdb.security;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SecurityConfigTest {

    @InjectMocks
    private SecurityConfig securityConfig;

    @Mock
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    @Mock
    HttpSecurity http;

    @Test
    void testSecurityFilterChain() throws Exception {
        when(http.csrf(any())).thenReturn(http);
        when(http.cors(any())).thenReturn(http);
        when(http.sessionManagement(any())).thenReturn(http);
        when(http.authorizeHttpRequests(any())).thenReturn(http);
        when(http.exceptionHandling(any())).thenReturn(http);
        when(http.logout(any())).thenReturn(http);
        when(http.addFilterBefore(any(), any())).thenReturn(http);

        securityConfig.securityFilterChain(http);

        verify(http).csrf(any());
        verify(http).cors(any());
        verify(http).sessionManagement(any());
        verify(http).authorizeHttpRequests(any());
        verify(http).exceptionHandling(any());
        verify(http).logout(any());
        verify(http).addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
    }
}