package org.technoready.meliecommerce.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.technoready.meliecommerce.entity.User;
import org.technoready.meliecommerce.exception.ResourceNotFoundException;
import org.technoready.meliecommerce.repository.UserRepository;

import java.util.List;

/**
 * Service class that handles business logic for user operations.
 * Manages user retrieval, creation, updating, and deletion.
 * DATE: 18 - October - 2025
 *
 * @author Jorge Armando Avila Carrillo | NAOID: 3310
 * @version 1.0
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    /**
     * Retrieves all users (active and inactive).
     *
     * @return List<User> - List of all users
     */
    public List<User> findAll() {
        log.info("Retrieving all users");
        List<User> users = userRepository.findAll();
        log.info("Retrieved {} users", users.size());
        return users;
    }

    /**
     * Retrieves only active users.
     *
     * @return List<User> - List of active users
     */
    public List<User> findAllIsActive() {
        log.info("Retrieving all active users");
        List<User> users = userRepository.findAllByActiveTrue();
        log.info("Retrieved {} active users", users.size());
        return users;
    }

    /**
     * Retrieves a specific user by their ID.
     *
     * @param id Long - The ID of the user
     * @return User - The user entity
     * @throws ResourceNotFoundException if user is not found
     */
    public User findById(Long id) {
        log.info("Retrieving user with id: {}", id);
        return userRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("User not found with id: {}", id);
                    return new ResourceNotFoundException("User", "id", id);
                });
    }

    /**
     * Creates and saves a new user.
     *
     * @param user User - The user to save
     * @return User - The saved user with generated ID
     */
    public User save(User user) {
        log.info("Creating new user: {}", user.getEmail());
        User savedUser = userRepository.save(user);
        log.info("User created successfully with id: {}", savedUser.getId());
        return savedUser;
    }

    /**
     * Updates an existing user with new information.
     *
     * @param user User - The user with updated data
     * @param id Long - The ID of the user to update
     * @return User - The updated user entity
     * @throws ResourceNotFoundException if user is not found
     */
    public User update(User user, Long id) {
        log.info("Attempting to update user with id: {}", id);

        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Cannot update - User not found with id: {}", id);
                    return new ResourceNotFoundException("User", "id", id);
                });

        existingUser.setEmail(user.getEmail());
        existingUser.setName(user.getName());
        existingUser.setLastName(user.getLastName());
        existingUser.setActive(user.isActive());

        User updatedUser = userRepository.save(existingUser);
        log.info("User with id: {} has been successfully updated", id);

        return updatedUser;
    }

    /**
     * Soft deletes a user by deactivating them.
     *
     * @param id Long - The ID of the user to delete
     * @throws ResourceNotFoundException if user is not found
     */
    public void delete(Long id) {
        log.info("Attempting to delete user with id: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Cannot delete - User not found with id: {}", id);
                    return new ResourceNotFoundException("User", "id", id);
                });

        user.setActive(false);
        userRepository.save(user);
        log.info("User with id: {} has been successfully deactivated", id);
    }
}
