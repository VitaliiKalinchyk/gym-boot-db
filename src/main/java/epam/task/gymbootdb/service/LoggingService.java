package epam.task.gymbootdb.service;

import org.springframework.stereotype.Service;

@Service
public interface LoggingService {

    void logInfoInterceptorPreHandle(String methodName, String uri, String queryString);

    void logInfoInterceptorPostHandle(int status, String message);

    void logDebugController(String message);

    void logDebugController(String message, String username);

    void logDebugService(String message);

    void logDebugService(String message, String username);

    void logWarnService(String message, String username);

    void logErrorHandler(String errorName, String message, String errorId);
}
