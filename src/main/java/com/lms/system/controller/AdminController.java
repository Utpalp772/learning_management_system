package com.lms.system.controller;

import com.lms.system.dto.UpdateRoleRequestDTO;
import com.lms.system.dto.UserSummaryDTO;
import com.lms.system.service.AdminService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @GetMapping("/users")
    public ResponseEntity<List<UserSummaryDTO>> getAllUsers() {
        List<UserSummaryDTO> users = adminService.getAllUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @PutMapping("/users/{userId}/role")
    public ResponseEntity<UserSummaryDTO> updateUserRole(
            @PathVariable Long userId,
            @Valid @RequestBody UpdateRoleRequestDTO requestDTO) {

        UserSummaryDTO updated = adminService.updateUserRole(userId, requestDTO);
        return new ResponseEntity<>(updated, HttpStatus.OK);
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        adminService.deleteUser(userId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
