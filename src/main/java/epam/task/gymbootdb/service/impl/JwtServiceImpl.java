package epam.task.gymbootdb.service.impl;

import epam.task.gymbootdb.entity.JwtBlacklist;
import epam.task.gymbootdb.repository.JwtBlacklistRepository;
import epam.task.gymbootdb.service.JwtService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtServiceImpl implements JwtService {

    private final JwtBlacklistRepository jwtBlacklistRepository;
    private final String secretKey;
    private final long jwtExpiration;

    public JwtServiceImpl(JwtBlacklistRepository jwtBlacklistRepository ,
                          @Value("${security.jwt.secret-key}") String secretKey ,
                          @Value("${security.jwt.expiration-time}") long jwtExpiration) {
        this.jwtBlacklistRepository = jwtBlacklistRepository;
        this.secretKey = secretKey;
        this.jwtExpiration = jwtExpiration;
    }

    @Override
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    @Override
    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    @Override
    public String generateToken(UserDetails userDetails, long expiration) {
        return buildToken(new HashMap<>(), userDetails, expiration);
    }

    @Override
    public boolean isTokenValid(String token, UserDetails userDetails) {
        String username = extractUsername(token);

        return (username.equals(userDetails.getUsername()))
                && !isTokenExpired(token)
                && isNotInBlacklist(token);
    }

    @Override
    public void saveToBlacklist(String token) {
        JwtBlacklist jwtBlacklist = new JwtBlacklist(token, extractExpiration(token));
        jwtBlacklistRepository.save(jwtBlacklist);
    }

    @Override
    public void deleteExpiredTokens() {
        jwtBlacklistRepository.deleteAllByExpirationTimeBefore(new Date());
    }

    private boolean isNotInBlacklist(String token) {
        return !jwtBlacklistRepository.existsById(token);
    }

    private String buildToken(Map<String, Object> extraClaims,
                              UserDetails userDetails,
                              long expiration
    ) {
        return Jwts.builder()
                .claims(extraClaims)
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey())
                .compact();
    }

    private String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return buildToken(extraClaims, userDetails, jwtExpiration);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
