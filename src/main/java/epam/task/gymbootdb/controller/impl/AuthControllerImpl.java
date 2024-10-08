package epam.task.gymbootdb.controller.impl;

import epam.task.gymbootdb.controller.AuthController;
import epam.task.gymbootdb.dto.JwtTokenDto;
import epam.task.gymbootdb.dto.UserCredentials;
import epam.task.gymbootdb.service.AuthService;
import epam.task.gymbootdb.service.JwtService;
import epam.task.gymbootdb.service.LoggingService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthControllerImpl implements AuthController {

    private final AuthService authService;
    private final LoggingService loggingService;
    private final JwtService jwtService;

    @Override
    @PostMapping("/login")
    public JwtTokenDto login(@Valid @RequestBody UserCredentials credentials) {
        String token = authService.authenticate(credentials);
        loggingService.logDebugController("logged in", credentials.getUsername());

        return new JwtTokenDto(token);
    }

    @Override
    @PostMapping("/logout")
    public String logout(@RequestHeader String authorization) {
        String token = getToken(authorization);

        jwtService.saveToBlacklist(token);
        loggingService.logDebugController("logged out", jwtService.extractUsername(token));

        return "Logout successful";
    }

    private String getToken(String authorization) {
        return authorization.substring(7);
    }
}
