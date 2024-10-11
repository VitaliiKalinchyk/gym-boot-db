package epam.task.gymbootdb.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.slf4j.MDC;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionIdFilterTest {

    public static final String TRANSACTION_ID = "transactionId";
    public static final String USER = "user";

    @InjectMocks
    private TransactionIdFilter transactionIdFilter;

    @Mock
    private ServletRequest request;
    @Mock
    private ServletResponse response;
    @Mock
    private FilterChain chain;

    @AfterEach
    public void setUp() {
        MDC.clear();
    }

    @Test
    void testTransactionIdIsSetAndClearedInMDC() throws IOException, ServletException {
        MDC.put(TRANSACTION_ID, "12345");
        MDC.put(USER, "username");

        transactionIdFilter.doFilter(request, response, chain);

        verify(chain).doFilter(request, response);
        assertNull(MDC.get(TRANSACTION_ID));
        assertNull(MDC.get(USER));
    }
}
