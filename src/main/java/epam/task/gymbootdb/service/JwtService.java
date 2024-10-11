package epam.task.gymbootdb.service;

import org.springframework.security.core.userdetails.UserDetails;

public interface JwtService {
    String extractUsername(String token);

    String generateToken(UserDetails userDetails);

    String generateToken(UserDetails userDetails, long expiration);

    boolean isTokenValid(String token, UserDetails userDetails);

    void saveToBlacklist(String token);

    void deleteExpiredTokens();
}
