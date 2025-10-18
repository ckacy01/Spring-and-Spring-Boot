package org.technoready.meliecommerce.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.technoready.meliecommerce.dto.SuccessResponseDTO;
import org.technoready.meliecommerce.entity.User;
import org.technoready.meliecommerce.service.UserService;

import java.util.List;


@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<SuccessResponseDTO<List<User>>> findAll(
            @RequestParam(required = false, defaultValue = "false") boolean activeOnly) {

        log.info("Controller: Received request to get all users (activeOnly: {})", activeOnly);

        List<User> users = activeOnly
                ? userService.findAllIsActive()
                : userService.findAll();

        String message = activeOnly
                ? String.format("Retrieved %d active users successfully", users.size())
                : String.format("Retrieved %d users successfully", users.size());

        SuccessResponseDTO<List<User>> response = SuccessResponseDTO.of(
                HttpStatus.OK.value(),
                message,
                users
        );

        log.info("Controller: Retrieved {} users", users.size());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SuccessResponseDTO<User>> findById(@PathVariable Long id) {
        log.info("Controller: Received request to get user {}", id);

        User user = userService.findById(id);

        SuccessResponseDTO<User> response = SuccessResponseDTO.of(
                HttpStatus.OK.value(),
                String.format("User %d retrieved successfully", id),
                user
        );

        log.info("Controller: User {} retrieved successfully", id);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<SuccessResponseDTO<User>> save(@RequestBody User user) {
        log.info("Controller: Received request to create user {}", user.getEmail());

        User savedUser = userService.save(user);

        SuccessResponseDTO<User> response = SuccessResponseDTO.of(
                HttpStatus.CREATED.value(),
                String.format("User '%s %s' created successfully with ID: %d",
                        savedUser.getName(), savedUser.getLastName(), savedUser.getId()),
                savedUser
        );

        log.info("Controller: User {} created successfully", savedUser.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<SuccessResponseDTO<Void>> delete(@PathVariable long id) {
        log.info("Controller: Received request to delete user {}", id);

        userService.delete(id);

        SuccessResponseDTO<Void> response = SuccessResponseDTO.of(
                HttpStatus.OK.value(),
                String.format("User %d has been successfully deactivated", id)
        );

        log.info("Controller: User {} deleted successfully", id);
        return ResponseEntity.ok(response);
    }


    @PutMapping("/{id}")
    public ResponseEntity<SuccessResponseDTO<User>> update(
            @RequestBody User user,
            @PathVariable long id) {

        log.info("Controller: Received request to update user {}", id);

        User updatedUser = userService.update(user, id);

        SuccessResponseDTO<User> response = SuccessResponseDTO.of(
                HttpStatus.OK.value(),
                String.format("User %d updated successfully", id),
                updatedUser
        );

        log.info("Controller: User {} updated successfully", id);
        return ResponseEntity.ok(response);
    }


}
