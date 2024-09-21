package epam.task.gymbootdb.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.NonNull;
import lombok.Setter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Setter
@Component
public class RestLoggingInterceptor implements HandlerInterceptor {

    public static final String TRANSACTION_ID = "transactionId";

    private Logger log = LoggerFactory.getLogger(RestLoggingInterceptor.class);

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request,
                             @NonNull HttpServletResponse response,
                             @NonNull Object handler) {
        String transactionId = MDC.get(TRANSACTION_ID);
        String method = request.getMethod();
        String uri = request.getRequestURI();
        String queryString = request.getQueryString() != null ? "?" + request.getQueryString() : "";

        log.info("Transaction ID: {}, HTTP Method: {}, Endpoint: {}{}", transactionId, method, uri, queryString);

        return true;
    }

    @Override
    public void afterCompletion(@NonNull HttpServletRequest request,
                                @NonNull HttpServletResponse response,
                                @NonNull Object handler,
                                Exception e) {
        String transactionId = MDC.get(TRANSACTION_ID);
        int status = response.getStatus();
        String message = (e != null) ? e.getMessage() : "Response OK";

        log.info("Transaction ID: {}, Response Status: {}, Message: {}", transactionId, status, message);
    }
}