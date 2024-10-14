package epam.task.gymbootdb.controller.impl;

import epam.task.gymbootdb.controller.AuthController;
import epam.task.gymbootdb.dto.JwtTokenDto;
import epam.task.gymbootdb.dto.UserCredentials;
import epam.task.gymbootdb.service.AuthService;
import epam.task.gymbootdb.service.JwtService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthControllerImpl extends AbstractController implements AuthController {

    private final AuthService authService;
    private final JwtService jwtService;

    @Override
    @PostMapping("/login")
    public JwtTokenDto login(@Valid @RequestBody UserCredentials credentials) {
        return new JwtTokenDto(authService.authenticate(credentials));
    }

    @Override
    @PostMapping("/logout")
    public void logout(@RequestHeader String authorization) {
        jwtService.saveToBlacklist(getToken(authorization));
    }

    private String getToken(String authorization) {
        return authorization.substring(7);
    }
}
