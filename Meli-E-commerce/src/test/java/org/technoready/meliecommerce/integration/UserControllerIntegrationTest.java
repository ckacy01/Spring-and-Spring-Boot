package org.technoready.meliecommerce.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.technoready.meliecommerce.controller.UserController;
import org.technoready.meliecommerce.entity.User;
import org.technoready.meliecommerce.exception.ResourceNotFoundException;
import org.technoready.meliecommerce.service.UserService;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@DisplayName("User Controller Integration Tests")
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "auto-test.enabled=false"
})
class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
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

    // SUCCESS CASES
    @Test
    @DisplayName("GET /api/user - Should retrieve all users successfully")
    void testFindAll_Success() throws Exception {
        // Arrange
        List<User> users = List.of(testUser);
        when(userService.findAll()).thenReturn(users);

        // Act & Assert
        mockMvc.perform(get("/api/user"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].name").value("Jorge"));
    }

    @Test
    @DisplayName("GET /api/user/{id} - Should retrieve user by ID successfully")
    void testFindById_Success() throws Exception {
        // Arrange
        when(userService.findById(1L)).thenReturn(testUser);

        // Act & Assert
        mockMvc.perform(get("/api/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.email").value("jorge@example.com"));
    }

    @Test
    @DisplayName("POST /api/user - Should create user successfully")
    void testSave_Success() throws Exception {
        // Arrange
        when(userService.save(any(User.class))).thenReturn(testUser);

        // Act & Assert
        mockMvc.perform(post("/api/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testUser)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value(201))
                .andExpect(jsonPath("$.data.name").value("Jorge"));
    }

    @Test
    @DisplayName("PUT /api/user/{id} - Should update user successfully")
    void testUpdate_Success() throws Exception {
        // Arrange
        User updatedUser = User.builder()
                .id(1L)
                .name("Updated Name")
                .lastName("Updated LastName")
                .email("updated@example.com")
                .active(true)
                .build();

        when(userService.update(any(User.class), eq(1L))).thenReturn(updatedUser);

        // Act & Assert
        mockMvc.perform(put("/api/user/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.name").value("Updated Name"));
    }

    @Test
    @DisplayName("DELETE /api/user/{id} - Should delete user successfully")
    void testDelete_Success() throws Exception {
        // Act & Assert
        mockMvc.perform(delete("/api/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").exists());
    }

    // FAILURE SCENARIOS
    @Test
    @DisplayName("GET /api/user/{id} - Should return 404 when user not found")
    void testFindById_NotFound() throws Exception {
        // Arrange
        when(userService.findById(999L))
                .thenThrow(new ResourceNotFoundException("User", "id", 999L));

        // Act & Assert
        mockMvc.perform(get("/api/user/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PUT /api/user/{id} - Should return 404 when updating non-existent user")
    void testUpdate_NotFound() throws Exception {
        // Arrange
        when(userService.update(any(User.class), eq(999L)))
                .thenThrow(new ResourceNotFoundException("User", "id", 999L));

        // Act & Assert
        mockMvc.perform(put("/api/user/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testUser)))
                .andExpect(status().isNotFound());
    }

    // EDGE CASES
    @Test
    @DisplayName("GET /api/user - Should return empty list when no users exist")
    void testFindAll_EmptyList() throws Exception {
        // Arrange
        when(userService.findAll()).thenReturn(new ArrayList<>());

        // Act & Assert
        mockMvc.perform(get("/api/user"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    @DisplayName("GET /api/user?activeOnly=true - Should retrieve only active users")
    void testFindAll_ActiveOnly() throws Exception {
        // Arrange
        List<User> activeUsers = List.of(testUser);
        when(userService.findAllIsActive()).thenReturn(activeUsers);

        // Act & Assert
        mockMvc.perform(get("/api/user")
                        .param("activeOnly", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].active").value(true));
    }
}
