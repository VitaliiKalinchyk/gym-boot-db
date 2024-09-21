package epam.task.gymbootdb.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import org.slf4j.MDC;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Component
public class RestLoggingInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request,
                             @NonNull HttpServletResponse response,
                             @NonNull Object handler) {
        String transactionId = MDC.get("transactionId");
        String method = request.getMethod();
        String uri = request.getRequestURI();
        String queryString = request.getQueryString() != null ? "?" + request.getQueryString() : "";

        log.info("Transaction ID: {}, HTTP Method: {}, Endpoint: {}{}", transactionId, method, uri, queryString);

        return true;
    }

    @Override
    public void afterCompletion(@NonNull HttpServletRequest request,
                                HttpServletResponse response,
                                @NonNull Object handler,
                                Exception e) {
        String transactionId = MDC.get("transactionId");
        int status = response.getStatus();
        String message = (e != null) ? e.getMessage() : "Response OK";

        log.info("Transaction ID: {}, Response Status: {}, Message: {}", transactionId, status, message);
    }
}