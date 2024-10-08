package epam.task.gymbootdb.service.impl;

import epam.task.gymbootdb.service.LoggingService;
import lombok.extern.slf4j.Slf4j;

import org.slf4j.MDC;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class LoggingServiceImpl implements LoggingService {

    private static final String TRANSACTION_ID = "transactionId";

    @Override
    public void logInfoInterceptorPreHandle(String methodName, String uri, String queryString) {
        log.info("Transaction ID: {}, HTTP Method: {}, Endpoint: {}{}, User: {}",
                getTransactionId(), methodName, uri, queryString, getUsername());
    }

    @Override
    public void logInfoInterceptorPostHandle(int status, String message) {
        log.info("Transaction ID: {}, Response Status: {}, Message: {}, User: {}",
                getTransactionId(), status, message, getUsername());
    }

    @Override
    public void logDebugController(String message) {
        logDebugController(message, getUsername());
    }

    @Override
    public void logDebugController(String message, String username) {
        log.debug("Transaction ID: {}, User (username = {}) {}. Controller layer",
                getTransactionId(), username, message);
    }

    @Override
    public void logDebugAdminController(String message, String username) {
        log.debug("Transaction ID: {},Admin {} User (username = {}). Controller layer",
                getTransactionId(), message, username);
    }

    @Override
    public void logDebugService(String message) {
        logDebugService(message, getUsername());

    }

    @Override
    public void logDebugService(String message, String username) {
        log.debug("Transaction ID: {}, User (username = {}) {}. Service layer",
                getTransactionId(), username, message);
    }

    @Override
    public void logWarnService(String message, String username) {
        log.warn("Transaction ID: {}, User (username = {}) {}. Service layer",
                getTransactionId(), username, message);
    }


    @Override
    public void logErrorHandler(String errorName, String message, String errorId) {
        log.error("TransactionId: {}. {}: {}. ErrorHandler. ErrorId: {}", getTransactionId(), errorName, message, errorId);
    }

    private String getTransactionId() {
        return MDC.get(TRANSACTION_ID);
    }

    private String getUsername() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if (username.equals("anonymousUser")) username = "unauthorized request";
        return username;
    }
}
