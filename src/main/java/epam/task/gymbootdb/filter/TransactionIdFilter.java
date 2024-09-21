package epam.task.gymbootdb.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;

import org.slf4j.MDC;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

@Component
public class TransactionIdFilter  implements Filter {

    private static final String TRANSACTION_ID = "transactionId";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        MDC.put(TRANSACTION_ID, UUID.randomUUID().toString());

        try {
            chain.doFilter(request, response);
        } finally {
            MDC.clear();
        }
    }
}