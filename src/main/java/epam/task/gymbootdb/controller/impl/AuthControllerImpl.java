package epam.task.gymbootdb.controller.impl;

import epam.task.gymbootdb.controller.AuthController;
import epam.task.gymbootdb.dto.JwtToken;
import epam.task.gymbootdb.dto.UserCredentials;
import epam.task.gymbootdb.service.AuthService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthControllerImpl implements AuthController {

    private static final String TRANSACTION_ID = "transactionId";

    private final AuthService authService;

    @Override
    @PostMapping("/login")
    public ResponseEntity<JwtToken> login(@Valid @RequestBody UserCredentials credentials) {
        String token = authService.authenticate(credentials);
        log.debug("User (username = {}) logged in. Controller layer. TransactionId: {}",
                credentials.getUsername(), MDC.get(TRANSACTION_ID));

        return ResponseEntity.ok(new JwtToken(token));
    }

    @Override
    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
        log.debug("User logged out. Controller layer. TransactionId: {}", MDC.get(TRANSACTION_ID));

        return ResponseEntity.ok("Logout successful");
    }
}
