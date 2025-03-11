package com.springboot.api.counselor.controller;

import java.util.List;

import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.springboot.api.counselor.service.KeycloakUserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class KeycloakUserController {

    private final KeycloakUserService keycloakUserService;

    @GetMapping
    public ResponseEntity<List<UserRepresentation>> getUsers() {
        return ResponseEntity.ok(keycloakUserService.getUsers());
    }

    @PostMapping
    public ResponseEntity<Void> createUser(@RequestBody CreateUserRequest request) {
        keycloakUserService.createUser(request.username(), request.email(), request.password());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable String userId) {
        keycloakUserService.deleteUser(userId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{userId}")
    public ResponseEntity<Void> updateUser(@PathVariable String userId, @RequestBody UpdateUserRequest request) {
        keycloakUserService.updateUser(userId, request.email());
        return ResponseEntity.ok().build();
    }

    record CreateUserRequest(String username, String email, String password) {
    }

    record UpdateUserRequest(String email) {
    }
}