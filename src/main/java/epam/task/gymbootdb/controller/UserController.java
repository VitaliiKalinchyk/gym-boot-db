package epam.task.gymbootdb.controller;

import epam.task.gymbootdb.dto.ChangePasswordRequest;
import epam.task.gymbootdb.dto.UserCredentials;
import epam.task.gymbootdb.service.UserService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/login")
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<Void> login(@Valid @RequestBody UserCredentials userCredentials){
        userService.matchCredentials(userCredentials);

        return ResponseEntity.ok().build();
    }

    @PutMapping
    public ResponseEntity<Void> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        System.out.println("here");
        userService.changePassword(request);

        return ResponseEntity.ok().build();
    }
}