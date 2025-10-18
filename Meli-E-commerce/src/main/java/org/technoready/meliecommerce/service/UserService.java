package org.technoready.meliecommerce.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.technoready.meliecommerce.entity.User;
import org.technoready.meliecommerce.exception.ResourceNotFoundException;
import org.technoready.meliecommerce.repository.UserRepository;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public List<User> findAll() {
        log.info("Retrieving all users");
        List<User> users = userRepository.findAll();
        log.info("Retrieved {} users", users.size());
        return users;
    }

    public List<User> findAllIsActive() {
        log.info("Retrieving all active users");
        List<User> users = userRepository.findAllByActiveTrue();
        log.info("Retrieved {} active users", users.size());
        return users;
    }

    public User findById(Long id) {
        log.info("Retrieving user with id: {}", id);
        return userRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("User not found with id: {}", id);
                    return new ResourceNotFoundException("User", "id", id);
                });
    }

    public User save(User user) {
        log.info("Creating new user: {}", user.getEmail());
        User savedUser = userRepository.save(user);
        log.info("User created successfully with id: {}", savedUser.getId());
        return savedUser;
    }

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
