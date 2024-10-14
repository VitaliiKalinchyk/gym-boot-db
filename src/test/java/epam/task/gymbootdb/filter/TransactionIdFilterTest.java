package epam.task.gymbootdb.filter;

import epam.task.gymbootdb.entity.User;
import epam.task.gymbootdb.repository.UserRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.slf4j.MDC;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionIdFilterTest {

    public static final String TRANSACTION_ID = "transactionId";
    public static final String USER = "user";

    @Mock
    private UserRepository userRepository;
    @Mock
    private ServletRequest request;
    @Mock
    private ServletResponse response;
    @Mock
    private FilterChain chain;
    @Mock
    private Authentication authentication;
    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private TransactionIdFilter transactionIdFilter;

    @BeforeEach
    public void init() {
        setUpSecurityContext();
    }

    @AfterEach
    public void tearDown() {
        MDC.clear();
        SecurityContextHolder.clearContext();
    }

    @Test
    void transactionIdIsSetAndClearedInMDC() throws IOException, ServletException {
        MDC.put(TRANSACTION_ID, "12345");
        MDC.put(USER, "username");

        when(userRepository.findByUsername(anyString())).thenReturn(getUser());

        transactionIdFilter.doFilter(request, response, chain);

        verify(chain).doFilter(request, response);
        assertNull(MDC.get(TRANSACTION_ID));
        assertNull(MDC.get(USER));
    }

    @Test
    void transactionIdIsSetAndClearedInMDCNoAuthentication() throws IOException, ServletException {
        MDC.put(TRANSACTION_ID, "12345");
        MDC.put(USER, "username");

        when(securityContext.getAuthentication()).thenReturn(null);

        transactionIdFilter.doFilter(request, response, chain);

        verify(chain).doFilter(request, response);
        assertNull(MDC.get(TRANSACTION_ID));
        assertNull(MDC.get(USER));
    }

    private void setUpSecurityContext() {
        SecurityContextHolder.setContext(securityContext);
        lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
        lenient().when(authentication.getName()).thenReturn("Joe.Doe");
    }

    private Optional<User> getUser() {
        return Optional.ofNullable(User.builder().id(1).build());
    }
}
