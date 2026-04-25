package com.concessio.crm.user.controller;

import com.concessio.crm.exceptions.ResourceNotFoundException;
import com.concessio.crm.user.model.User;
import com.concessio.crm.user.dto.PaginatedResponse;
import com.concessio.crm.user.dto.UserRequestDTO;
import com.concessio.crm.user.dto.UserResponseDTO;
import com.concessio.crm.user.mapper.UserMapper;
import com.concessio.crm.user.service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final UserService service;
    private final UserMapper userMapper;

    public UserController(UserService service, UserMapper userMapper) {
        this.service = service;
        this.userMapper = userMapper;
    }

    @PostMapping
    public UserResponseDTO create(@Valid @RequestBody UserRequestDTO request) {
        logger.info("User created: " + request.toString());
        return service.createUser(userMapper.toEntity(request));
    }

    @GetMapping
    public PaginatedResponse<UserResponseDTO> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy) {
        return service.getAllUsers(page, size, sortBy);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> findById(@PathVariable Long id) {
        UserResponseDTO user = service.getUserById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return ResponseEntity.ok(user);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
