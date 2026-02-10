package com.example.Travel.controller;

import com.example.Travel.dto.LoginDTO;
import com.example.Travel.dto.UserDTO;
import com.example.Travel.dto.UserRegistrationDTO;
import com.example.Travel.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:4200")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody UserDTO userDTO) {
        try {
            String result = authService.register(userDTO);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", result);
            response.put("timestamp", System.currentTimeMillis());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            errorResponse.put("timestamp", System.currentTimeMillis());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @PostMapping("/register/v2")
    public ResponseEntity<Map<String, Object>> registerV2(@RequestBody UserRegistrationDTO registrationDTO) {
        try {
            String result = authService.registerUser(registrationDTO);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", result);
            response.put("timestamp", System.currentTimeMillis());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            errorResponse.put("timestamp", System.currentTimeMillis());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginDTO loginDTO) {
        try {
            String token = authService.login(loginDTO);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("token", token);
            response.put("message", "Login successful");
            response.put("timestamp", System.currentTimeMillis());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            errorResponse.put("timestamp", System.currentTimeMillis());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @PostMapping("/validate")
    public ResponseEntity<Map<String, Object>> validateToken(@RequestHeader("Authorization") String token) {
        try {
            // Remove "Bearer " prefix if present
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }

            authService.validateToken(token);
            Map<String, Object> response = new HashMap<>();
            response.put("valid", true);
            response.put("message", "Token is valid");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("valid", false);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(401).body(errorResponse);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout() {
        authService.logout();
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Logged out successfully");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/init-admin")
    public ResponseEntity<Map<String, Object>> initAdmin() {
        try {
            String result = authService.initDefaultAdmin();
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", result);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @GetMapping("/check-username/{username}")
    public ResponseEntity<Boolean> checkUsername(@PathVariable String username) {
        boolean exists = authService.userExists(username);
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/check-email/{email}")
    public ResponseEntity<Boolean> checkEmail(@PathVariable String email) {
        boolean exists = authService.emailExists(email);
        return ResponseEntity.ok(exists);
    }
}

//package com.example.Travel.controller;
//
//import com.example.Travel.dto.LoginDTO;
//import com.example.Travel.dto.UserDTO;
//import com.example.Travel.service.AuthService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.HashMap;
//import java.util.Map;
//
//@RestController
//@RequestMapping("/api/auth")
//@CrossOrigin(origins = "http://localhost:4200")
//public class AuthController {
//
//    @Autowired
//    private AuthService authService;
//
//    @PostMapping("/register")
//    public ResponseEntity<String> register(@RequestBody UserDTO userDTO) {
//        String result = authService.register(userDTO);
//        return ResponseEntity.ok(result);
//    }
//
//    @PostMapping("/login")
//    public ResponseEntity<Map<String, String>> login(@RequestBody LoginDTO loginDTO) {
//        String token = authService.login(loginDTO);
//        Map<String, String> response = new HashMap<>();
//        response.put("token", token);
//        return ResponseEntity.ok(response);
//    }
//
////    @PostMapping("/login")
////    public ResponseEntity<String> login(@RequestBody LoginDTO loginDTO) {
////        String result = authService.login(loginDTO);
////        return ResponseEntity.ok(result);
////    }
//}
