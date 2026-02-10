package com.example.Travel.controller;

import com.example.Travel.dto.UserDTO;
import com.example.Travel.dto.UserRegistrationDTO;
import com.example.Travel.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:4200")
public class UserController {

    @Autowired
    private UserService userService;

    // Get current user profile
//    @GetMapping("/profile")
//    public ResponseEntity<UserDTO> getCurrentUser(Principal principal) {
//        String username = principal.getName();
//        UserDTO user = userService.getUserByUsername(username);
//        return ResponseEntity.ok(user);
//    }
//    @GetMapping("/profile")
//    public ResponseEntity<UserDTO> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
//        if (userDetails == null) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
//        }
//        UserDTO user = userService.getUserByUsername(userDetails.getUsername());
//        return ResponseEntity.ok(user);
//    }
    @GetMapping("/profile")
    public ResponseEntity<UserDTO> getCurrentUser(Principal principal) {
        if (principal == null) {
            System.out.println("❌ Principal is null - no authentication!");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        System.out.println("✅ Principal: " + principal.getName());
        UserDTO user = userService.getUserByUsername(principal.getName());
        return ResponseEntity.ok(user);
    }

    // Update current user profile
    @PutMapping("/profile")
    public ResponseEntity<UserDTO> updateProfile(Principal principal,
                                                 @RequestBody UserRegistrationDTO updateDTO) {
        String username = principal.getName();
        UserDTO updatedUser = userService.updateUserByUsername(username, updateDTO);
        return ResponseEntity.ok(updatedUser);
    }

    // Register new user (duplicate - consider keeping only in AuthController)
    @PostMapping("/register")
    public ResponseEntity<UserDTO> registerUser(@RequestBody UserRegistrationDTO registrationDTO) {
        UserDTO user = userService.createUser(registrationDTO);
        return ResponseEntity.ok(user);
    }

    // Get user by ID (admin only)
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUser(@PathVariable Long id) {
        UserDTO user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    // Get all users (admin only)
    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    // Update user by ID (admin only)
    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id,
                                              @RequestBody UserRegistrationDTO updateDTO) {
        UserDTO updatedUser = userService.updateUser(id, updateDTO);
        return ResponseEntity.ok(updatedUser);
    }

    // Delete user (admin only)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    // Check if username exists
    @GetMapping("/check-username/{username}")
    public ResponseEntity<Boolean> checkUsernameExists(@PathVariable String username) {
        boolean exists = userService.usernameExists(username);
        return ResponseEntity.ok(exists);
    }

    // Check if email exists
    @GetMapping("/check-email/{email}")
    public ResponseEntity<Boolean> checkEmailExists(@PathVariable String email) {
        boolean exists = userService.emailExists(email);
        return ResponseEntity.ok(exists);
    }
}

//package com.example.Travel.controller;
//
//import com.example.Travel.dto.UserDTO;
//import com.example.Travel.dto.UserRegistrationDTO;
//import com.example.Travel.service.UserService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequestMapping("/api/users")
//@CrossOrigin(origins = "http://localhost:4200")
//public class UserController {
//
//    @Autowired
//    private UserService userService;
//
//    @PostMapping("/register")
//    public ResponseEntity<UserDTO> registerUser(@RequestBody UserRegistrationDTO registrationDTO) {
//        UserDTO user = userService.createUser(registrationDTO);
//        return ResponseEntity.ok(user);
//    }
//
//    @GetMapping("/{id}")
//    public ResponseEntity<UserDTO> getUser(@PathVariable Long id) {
//        UserDTO user = userService.getUserById(id);
//        return ResponseEntity.ok(user);
//    }
//
//    @PutMapping("/{id}")
//    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id, @RequestBody UserRegistrationDTO updateDTO) {
//        UserDTO updatedUser = userService.updateUser(id, updateDTO);
//        return ResponseEntity.ok(updatedUser);
//    }
//
//    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
//        userService.deleteUser(id);
//        return ResponseEntity.noContent().build();
//    }
//}
