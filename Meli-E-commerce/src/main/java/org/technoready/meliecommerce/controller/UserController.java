package org.technoready.meliecommerce.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.technoready.meliecommerce.dto.SuccessResponseDTO;
import org.technoready.meliecommerce.entity.User;
import org.technoready.meliecommerce.exception.ResourceNotFoundException;
import org.technoready.meliecommerce.service.UserService;

import java.util.List;

/**
 * REST Controller that manages user-related operations.
 * Provides endpoints for retrieving, creating, updating, and deleting users.
 * DATE: 22 - October - 2025
 *
 * @author Jorge Armando Avila Carrillo | NAOID: 3310
 * @version 1.3
 */

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Users", description = "Endpoints for managing users in the system")
public class UserController {

    private final UserService userService;

    /**
     * Retrieves all users or only active users based on the activeOnly parameter.
     *
     * @param activeOnly boolean - Flag to retrieve only active users (default: false)
     * @return ResponseEntity with SuccessResponseDTO containing list of Users
     */
    @Operation(
            summary = "Get all users",
            description = "Retrieves all users or only active users if the activeOnly parameter is true."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Users retrieved successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
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

    /**
     * Retrieves a specific user by their ID.
     *
     * @param id Long - The ID of the user to retrieve
     * @return ResponseEntity with SuccessResponseDTO containing the User
     * @throws ResourceNotFoundException if the user is not found
     */
    @Operation(
            summary = "Get user by ID",
            description = "Retrieves detailed information about a specific user."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
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

    /**
     * Creates a new user with the provided information.
     *
     * @param user User - The user object to be created
     * @return ResponseEntity with SuccessResponseDTO containing the created User
     */
    @Operation(
            summary = "Create new user",
            description = "Creates a new user with the given name, email, and other details."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
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

    /**
     * Soft deletes (deactivates) a user by their ID.
     *
     * @param id long - The ID of the user to delete
     * @return ResponseEntity with SuccessResponseDTO indicating successful deactivation
     * @throws ResourceNotFoundException if the user is not found
     */
    @Operation(
            summary = "Delete user",
            description = "Soft deletes (deactivates) a user based on their ID."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User deleted successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
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

    /**
     * Updates an existing user with new information.
     *
     * @param user User - The user object with updated information
     * @param id long - The ID of the user to update
     * @return ResponseEntity with SuccessResponseDTO containing the updated User
     * @throws ResourceNotFoundException if the user is not found
     */
    @Operation(
            summary = "Update user",
            description = "Updates an existing user's information such as name, email, or status."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User updated successfully"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
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
