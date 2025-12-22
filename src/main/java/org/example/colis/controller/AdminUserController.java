package org.example.colis.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.example.colis.dto.CreateUserRequest;
import org.example.colis.dto.UpdateUserRequest;
import org.example.colis.dto.UserDTO;
import org.example.colis.enums.Specialite;
import org.example.colis.model.User;
import org.example.colis.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@Tag(name = "Admin - User Management", description = "Admin endpoints for managing users and transporteurs")
@SecurityRequirement(name = "Bearer Authentication")
public class AdminUserController {

    @Autowired
    private UserService userService;

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all users", description = "Get paginated list of all users")
    public ResponseEntity<Page<UserDTO>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<UserDTO> users = userService.getAllUsers(pageable);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/transporteurs")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all transporteurs", description = "Get paginated list of all transporteurs")
    public ResponseEntity<Page<UserDTO>> getAllTransporteurs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Specialite specialite) {
        Pageable pageable = PageRequest.of(page, size);
        Page<UserDTO> transporteurs;

        if (specialite != null) {
            transporteurs = userService.getTransporteursBySpecialite(specialite, pageable);
        } else {
            transporteurs = userService.getAllTransporteurs(pageable);
        }

        return ResponseEntity.ok(transporteurs);
    }

    @PostMapping("/transporteurs")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create transporteur", description = "Create a new transporteur")
    public ResponseEntity<UserDTO> createTransporteur(@Valid @RequestBody CreateUserRequest request) {
        UserDTO created = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/transporteurs/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update transporteur", description = "Update an existing transporteur")
    public ResponseEntity<UserDTO> updateTransporteur(
            @PathVariable String id,
            @Valid @RequestBody UpdateUserRequest request) {
        UserDTO updated = userService.updateUser(id, request);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/transporteurs/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete transporteur", description = "Delete a transporteur")
    public ResponseEntity<Void> deleteTransporteur(@PathVariable String id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/users/{id}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Activate user", description = "Activate a deactivated user account")
    public ResponseEntity<UserDTO> activateUser(@PathVariable String id) {
        UserDTO activated = userService.activateUser(id);
        return ResponseEntity.ok(activated);
    }

    @PutMapping("/users/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Deactivate user", description = "Deactivate a user account - user will not be able to login")
    public ResponseEntity<UserDTO> deactivateUser(
            @PathVariable String id,
            @AuthenticationPrincipal User currentUser) {
        UserDTO deactivated = userService.deactivateUser(id, currentUser.getId());
        return ResponseEntity.ok(deactivated);
    }
}
