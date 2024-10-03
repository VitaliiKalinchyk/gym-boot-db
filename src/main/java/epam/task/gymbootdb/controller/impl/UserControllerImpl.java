package epam.task.gymbootdb.controller.impl;

import epam.task.gymbootdb.controller.UserController;
import epam.task.gymbootdb.dto.ChangePasswordRequest;
import epam.task.gymbootdb.service.UserService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserControllerImpl implements UserController {

    private final UserService userService;

    @Override
    public ResponseEntity<Void> changeActiveStatus(String username) {
        userService.changeStatus(username);

        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Void> changePassword(ChangePasswordRequest request) {
        userService.changePassword(request);

        return ResponseEntity.ok().build();
    }
}
