package epam.task.gymbootdb.controller.impl;

import epam.task.gymbootdb.controller.UserController;
import epam.task.gymbootdb.dto.ChangePasswordRequest;
import epam.task.gymbootdb.service.LoggingService;
import epam.task.gymbootdb.service.UserService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserControllerImpl implements UserController {

    private final UserService userService;
    private final LoggingService loggingService;

    @Override
    @PatchMapping("/status")
    public ResponseEntity<Void> changeActiveStatus() {
        userService.changeStatus(getUsername());
        loggingService.logDebugController("changed it's active status");

        return ResponseEntity.ok().build();
    }

    @Override
    @PutMapping("/change-password")
    public ResponseEntity<Void> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        request.getUserCredentials().setUsername(getUsername());

        userService.changePassword(request);
        loggingService.logDebugController("changed it's password");

        return ResponseEntity.ok().build();
    }

    private static String getUsername() {
        return SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();
    }
}
