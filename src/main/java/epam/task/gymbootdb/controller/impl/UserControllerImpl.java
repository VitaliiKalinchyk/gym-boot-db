package epam.task.gymbootdb.controller.impl;

import epam.task.gymbootdb.controller.UserController;
import epam.task.gymbootdb.dto.ChangePasswordRequest;
import epam.task.gymbootdb.service.UserService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserControllerImpl implements UserController {

    private final UserService userService;

    @Override
    @PatchMapping("/status")
    public void changeUserActiveStatus() {
        userService.changeStatus(getUsername());
    }

    @Override
    @PutMapping("/change-password")
    public void changeUserPassword(@Valid @RequestBody ChangePasswordRequest request) {
        request.getUserCredentials().setUsername(getUsername());

        userService.changePassword(request);
    }

    private static String getUsername() {
        return SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();
    }
}
