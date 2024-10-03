package epam.task.gymbootdb.controller.impl;

import epam.task.gymbootdb.controller.UserController;
import epam.task.gymbootdb.dto.ChangePasswordRequest;
import epam.task.gymbootdb.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
public class UserControllerImpl implements UserController {

    private static final String TRANSACTION_ID = "transactionId";

    private final UserService userService;

    @Override
    @PatchMapping("/status")
    public ResponseEntity<Void> changeActiveStatus() {
        String username = getUsername();

        userService.changeStatus(username);
        log.debug("User (username = {}) changed his active status. Controller layer. TransactionId: {}",
                username, MDC.get(TRANSACTION_ID));

        return ResponseEntity.ok().build();
    }

    @Override
    @PutMapping("/change-password")
    public ResponseEntity<Void> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        String username = getUsername();
        request.getUserCredentials().setUsername(username);

        userService.changePassword(request);
        log.debug("User (username = {}) changed his password. Controller layer. TransactionId: {}",
                username, MDC.get(TRANSACTION_ID));

        return ResponseEntity.ok().build();
    }

    private static String getUsername() {
        return SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();
    }
}
