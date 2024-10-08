package epam.task.gymbootdb.security;

import epam.task.gymbootdb.dto.GymUserDetails;
import epam.task.gymbootdb.entity.User;
import epam.task.gymbootdb.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Mock
    private JwtService jwtService;
    @Mock
    private UserDetailsService userDetailsService;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private FilterChain filterChain;

    UserDetails userDetails;

    @BeforeEach
    void setup() {
        User user = User.builder().username("testUser").password("passwords").roles(new HashSet<>()).build();
        userDetails = new GymUserDetails(user);
        SecurityContextHolder.clearContext();
    }

    @AfterAll
    static void setOff() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void doFilterInternalShouldProceedWithoutJwtAuthenticationWhenJwtIsNull() throws Exception {
        when(request.getHeader("Authorization")).thenReturn(null);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(jwtService, userDetailsService);
    }

    @Test
    void doFilterInternalShouldAuthenticateUserWhenJwtIsValid() throws Exception {
        String jwt = "valid.jwt.token";
        String username = "testUser";

        when(request.getHeader("Authorization")).thenReturn("Bearer " + jwt);
        when(jwtService.extractUsername(jwt)).thenReturn(username);
        when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);
        when(jwtService.isTokenValid(jwt, userDetails)).thenReturn(true);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        System.out.println(authentication.getName());

        assertNotNull(authentication);
        assertEquals(username, authentication.getName());
    }

    @Test
    void doFilterInternalShouldNotAuthenticateUserWhenJwtIsInvalid() throws Exception {
        String jwt = "invalid.jwt.token";
        String username = "testUser";

        when(request.getHeader("Authorization")).thenReturn("Bearer " + jwt);
        when(jwtService.extractUsername(jwt)).thenReturn(username);
        when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);
        when(jwtService.isTokenValid(jwt, userDetails)).thenReturn(false);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNull(authentication);
    }
}