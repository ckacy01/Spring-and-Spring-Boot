package org.technoready.meliecommerce.service_unit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.technoready.meliecommerce.entity.User;
import org.technoready.meliecommerce.exception.ResourceNotFoundException;
import org.technoready.meliecommerce.repository.UserRepository;
import org.technoready.meliecommerce.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("User Service Unit Tests")
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "auto-test.enabled=false"
})
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .name("Jorge")
                .lastName("Avila")
                .email("jorge@example.com")
                .active(true)
                .build();
    }

    // SUCCESS CASES (R28)
    @Test
    @DisplayName("Should retrieve all users successfully")
    void testFindAll_Success() {
        // Arrange
        List<User> users = List.of(testUser);
        when(userRepository.findAll()).thenReturn(users);

        // Act
        List<User> result = userService.findAll();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Jorge", result.get(0).getName());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should retrieve all active users successfully")
    void testFindAllIsActive_Success() {
        // Arrange
        List<User> activeUsers = List.of(testUser);
        when(userRepository.findAllByActiveTrue()).thenReturn(activeUsers);

        // Act
        List<User> result = userService.findAllIsActive();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.get(0).isActive());
        verify(userRepository, times(1)).findAllByActiveTrue();
    }

    @Test
    @DisplayName("Should find user by ID successfully")
    void testFindById_Success() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // Act
        User result = userService.findById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("jorge@example.com", result.getEmail());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should save user successfully")
    void testSave_Success() {
        // Arrange
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        User result = userService.save(testUser);

        // Assert
        assertNotNull(result);
        assertEquals("Jorge", result.getName());
        assertEquals("jorge@example.com", result.getEmail());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Should update user successfully")
    void testUpdate_Success() {
        // Arrange
        User updatedUser = User.builder()
                .name("Updated Name")
                .lastName("Updated LastName")
                .email("updated@example.com")
                .active(true)
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        // Act
        User result = userService.update(updatedUser, 1L);

        // Assert
        assertNotNull(result);
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Should delete user successfully (soft delete)")
    void testDelete_Success() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        userService.delete(1L);

        // Assert
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).save(any(User.class));
    }

    // FAILURE SCENARIOS (R26)
    @Test
    @DisplayName("Should throw exception when user not found by ID")
    void testFindById_NotFound() {
        // Arrange
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            userService.findById(999L);
        });
        verify(userRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent user")
    void testDelete_UserNotFound() {
        // Arrange
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            userService.delete(999L);
        });
        verify(userRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent user")
    void testUpdate_UserNotFound() {
        // Arrange
        User updatedUser = User.builder()
                .name("Updated Name")
                .build();

        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            userService.update(updatedUser, 999L);
        });
        verify(userRepository, times(1)).findById(999L);
    }

    // EDGE CASES (R27)
    @Test
    @DisplayName("Should return empty list when no users exist")
    void testFindAll_EmptyList() {
        // Arrange
        when(userRepository.findAll()).thenReturn(new ArrayList<>());

        // Act
        List<User> result = userService.findAll();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return empty list when no active users exist")
    void testFindAllIsActive_EmptyList() {
        // Arrange
        when(userRepository.findAllByActiveTrue()).thenReturn(new ArrayList<>());

        // Act
        List<User> result = userService.findAllIsActive();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should handle user with special characters in email")
    void testSave_SpecialCharactersInEmail() {
        // Arrange
        User userWithSpecialEmail = User.builder()
                .name("Test")
                .lastName("User")
                .email("test+special@example.com")
                .active(true)
                .build();

        when(userRepository.save(any(User.class))).thenReturn(userWithSpecialEmail);

        // Act
        User result = userService.save(userWithSpecialEmail);

        // Assert
        assertNotNull(result);
        assertEquals("test+special@example.com", result.getEmail());
    }
}