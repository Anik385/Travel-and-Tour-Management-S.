package com.example.Travel.service;

import com.example.Travel.Security.JwtUtil;
import com.example.Travel.dto.LoginDTO;
import com.example.Travel.dto.UserDTO;
import com.example.Travel.dto.UserRegistrationDTO;
import com.example.Travel.entity.Role;
import com.example.Travel.entity.User;
import com.example.Travel.repository.RoleRepository;
import com.example.Travel.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
@Transactional
public class AuthService {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * User login with authentication
     */
    public String login(LoginDTO loginDTO) {
        System.out.println("🔐 Login attempt for username: " + loginDTO.getUsername());

        try {
            // Authenticate user with Spring Security
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginDTO.getUsername(),
                            loginDTO.getPassword()
                    )
            );

            // Set authentication in security context
            SecurityContextHolder.getContext().setAuthentication(authentication);
            System.out.println("✅ Authentication successful for: " + loginDTO.getUsername());

            // Load user from database to get roles and details
            User user = userRepository.findByUsername(loginDTO.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found: " + loginDTO.getUsername()));

            // Log user details for debugging
            System.out.println("👤 Found user: " + user.getUsername());
            System.out.println("🎭 User ID: " + user.getId());
            System.out.println("📧 User Email: " + user.getEmail());
            System.out.println("🛡️ User Roles: " + user.getRoles());

            // Generate JWT token with user details including roles
            String token = jwtUtil.generateTokenFromUser(user);
            System.out.println("🔑 Generated JWT token for user: " + user.getUsername());
            System.out.println("📝 Token length: " + token.length());

            // Optional: Decode and log token for debugging
            try {
                String[] parts = token.split("\\.");
                if (parts.length >= 2) {
                    String payload = new String(java.util.Base64.getUrlDecoder().decode(parts[1]));
                    System.out.println("📄 Token payload: " + payload);
                }
            } catch (Exception e) {
                System.out.println("⚠️ Could not decode token: " + e.getMessage());
            }

            return token;

        } catch (BadCredentialsException e) {
            System.out.println("❌ Invalid credentials for: " + loginDTO.getUsername());
            throw new RuntimeException("Invalid username or password");

        } catch (AuthenticationException e) {
            System.out.println("❌ Authentication failed: " + e.getMessage());
            throw new RuntimeException("Authentication failed: " + e.getMessage());

        } catch (Exception e) {
            System.out.println("❌ Login error: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Login failed: " + e.getMessage());
        }
    }

    /**
     * User registration
     */
    public String register(UserDTO userDTO) {
        System.out.println("📝 Registration attempt for: " + userDTO.getUsername());

        // Check if username already exists
        if (userRepository.existsByUsername(userDTO.getUsername())) {
            System.out.println("❌ Username already exists: " + userDTO.getUsername());
            throw new RuntimeException("Username already exists");
        }

        // Check if email already exists
        if (userRepository.existsByEmail(userDTO.getEmail())) {
            System.out.println("❌ Email already exists: " + userDTO.getEmail());
            throw new RuntimeException("Email already exists");
        }

        // Create new user
        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setPhone(userDTO.getPhone());
        user.setEnabled(true);
        user.setAccountNonExpired(true);
        user.setCredentialsNonExpired(true);
        user.setAccountNonLocked(true);

        // Assign default USER role
        Role userRole = roleRepository.findByName("ROLE_USER").orElse(null);
        if (userRole == null) {
            System.out.println("⚠️ ROLE_USER not found, creating default roles...");

            // Create default roles if they don't exist
            Role newUserRole = new Role();
            newUserRole.setName("ROLE_USER");
            newUserRole.setDescription("User role");
            userRole = roleRepository.save(newUserRole);
            System.out.println("✅ Created ROLE_USER");
        }

        Set<Role> roles = new HashSet<>();
        roles.add(userRole);

        // If this is the first admin user (based on username or email), also assign ADMIN role
        if (userDTO.getUsername().equals("admin123") ||
                userDTO.getEmail().toLowerCase().contains("admin")) {

            Role adminRole = roleRepository.findByName("ROLE_ADMIN").orElse(null);
            if (adminRole == null) {
                Role newAdminRole = new Role();
                newAdminRole.setName("ROLE_ADMIN");
                newAdminRole.setDescription("Administrator role");
                adminRole = roleRepository.save(newAdminRole);
                System.out.println("✅ Created ROLE_ADMIN");
            }

            roles.add(adminRole);
            System.out.println("👑 Assigned ADMIN role to: " + userDTO.getUsername());
        }

        user.setRoles(roles);

        // Save user to database
        User savedUser = userRepository.save(user);
        System.out.println("✅ User registered successfully: " + savedUser.getUsername());
        System.out.println("👤 User ID: " + savedUser.getId());
        System.out.println("🛡️ Assigned roles: " + savedUser.getRoles());

        return "User registered successfully. User ID: " + savedUser.getId();
    }

    /**
     * Alternative registration method using UserRegistrationDTO
     */
    public String registerUser(UserRegistrationDTO registrationDTO) {
        System.out.println("📝 Registration via UserRegistrationDTO for: " + registrationDTO.getUsername());

        // Convert UserRegistrationDTO to UserDTO
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername(registrationDTO.getUsername());
        userDTO.setFirstName(registrationDTO.getFirstName());
        userDTO.setLastName(registrationDTO.getLastName());
        userDTO.setEmail(registrationDTO.getEmail());
        userDTO.setPassword(registrationDTO.getPassword());
        userDTO.setPhone(registrationDTO.getPhone());

        return register(userDTO);
    }

    /**
     * Validate token and get user details
     */
    public User validateToken(String token) {
        try {
            if (!jwtUtil.validateToken(token)) {
                throw new RuntimeException("Invalid token");
            }

            String username = jwtUtil.extractUsername(token);
            return userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

        } catch (Exception e) {
            System.out.println("❌ Token validation failed: " + e.getMessage());
            throw new RuntimeException("Token validation failed");
        }
    }

    /**
     * Check if user exists by username
     */
    public boolean userExists(String username) {
        return userRepository.existsByUsername(username);
    }

    /**
     * Check if email exists
     */
    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    /**
     * Get user by username
     */
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
    }

    /**
     * Refresh token
     */
    public String refreshToken(String oldToken) {
        try {
            if (!jwtUtil.validateToken(oldToken)) {
                throw new RuntimeException("Invalid token");
            }

            String username = jwtUtil.extractUsername(oldToken);
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            return jwtUtil.generateTokenFromUser(user);

        } catch (Exception e) {
            System.out.println("❌ Token refresh failed: " + e.getMessage());
            throw new RuntimeException("Token refresh failed");
        }
    }

    /**
     * Logout - clear authentication context
     */
    public void logout() {
        SecurityContextHolder.clearContext();
        System.out.println("✅ User logged out successfully");
    }

    /**
     * Initialize default admin user (for testing/development)
     */
    @Transactional
    public String initDefaultAdmin() {
        System.out.println("👑 Initializing default admin user...");

        // Check if admin already exists
        if (userRepository.existsByUsername("admin123")) {
            System.out.println("✅ Admin user already exists");
            return "Admin user already exists";
        }

        // Create admin user DTO
        UserDTO adminDTO = new UserDTO();
        adminDTO.setUsername("admin123");
        adminDTO.setFirstName("Admin");
        adminDTO.setLastName("User");
        adminDTO.setEmail("admin@travel.com");
        adminDTO.setPassword("admin123");
        adminDTO.setPhone("+1234567890");

        // Register admin user
        try {
            String result = register(adminDTO);
            System.out.println("✅ Default admin user created successfully");
            return result;
        } catch (Exception e) {
            System.out.println("❌ Failed to create admin user: " + e.getMessage());
            return "Failed to create admin user: " + e.getMessage();
        }
    }

    /**
     * Get current authenticated user from security context
     */
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("No authenticated user found");
        }

        String username = authentication.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
    }

    /**
     * Check if current user has admin role
     */
    public boolean isCurrentUserAdmin() {
        try {
            User user = getCurrentUser();
            return user.getRoles().stream()
                    .anyMatch(role -> "ROLE_ADMIN".equals(role.getName()));
        } catch (Exception e) {
            return false;
        }
    }
}

//package com.example.Travel.service;
//
//import com.example.Travel.Security.JwtUtil;
//import com.example.Travel.dto.LoginDTO;
//import com.example.Travel.dto.UserDTO;
//import com.example.Travel.entity.Role;
//import com.example.Travel.entity.User;
//import com.example.Travel.repository.RoleRepository;
//import com.example.Travel.repository.UserRepository;
//import jakarta.transaction.Transactional;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Service;
//
//import java.util.Set;
//
//@Service
//@Transactional
//public class AuthService {
//    @Autowired
//    private JwtUtil jwtUtil; // Add this line with other @Autowired fields
//
//    @Autowired
//    private AuthenticationManager authenticationManager;
//
//    @Autowired
//    private UserRepository userRepository;
//
//    @Autowired
//    private RoleRepository roleRepository;
//
//    @Autowired
//    private PasswordEncoder passwordEncoder;
//
//    // For JWT - add later: @Autowired private JwtUtil jwtUtil;
//
////    public String login(LoginDTO loginDTO) {
////        Authentication authentication = authenticationManager.authenticate(
////                new UsernamePasswordAuthenticationToken(
////                        loginDTO.getUsername(),
////                        loginDTO.getPassword()
////                )
////        );
////
////        SecurityContextHolder.getContext().setAuthentication(authentication);
////
////        // Generate JWT token (line 47-49)
////        String token = jwtUtil.generateToken(loginDTO.getUsername());
////        return token; // Return token instead of message
////    }
//public String login(LoginDTO loginDTO) {
//    System.out.println("Login attempt: " + loginDTO.getUsername());
//    // Authenticate user
//    Authentication authentication = authenticationManager.authenticate(
//            new UsernamePasswordAuthenticationToken(
//                    loginDTO.getUsername(),
//                    loginDTO.getPassword()
//            )
//    );
//
//    SecurityContextHolder.getContext().setAuthentication(authentication);
//
//    // Load user to get ID for token
//    User user = userRepository.findByUsername(loginDTO.getUsername())
//            .orElseThrow(() -> new RuntimeException("User not found"));
//
//    // Generate JWT token with user details
//    String token = jwtUtil.generateTokenFromUser(user);
//
//    return token; // Returns actual JWT token like "eyJhbGciOiJIUzI1NiIs..."
//}
//
//    public String register(UserDTO userDTO) {
//        if (userRepository.existsByUsername(userDTO.getUsername())) {
//            throw new RuntimeException("Username exists");
//        }
//
//        User user = new User();
//        user.setUsername(userDTO.getUsername());
//        user.setEmail(userDTO.getEmail());
//        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
//        user.setFirstName(userDTO.getFirstName());
//        user.setLastName(userDTO.getLastName());
//        user.setPhone(userDTO.getPhone());
//        user.setEnabled(true);
//        user.setAccountNonExpired(true);
//        user.setCredentialsNonExpired(true);
//        user.setAccountNonLocked(true);
//
//// Line 2: Assign default role
//        Role userRole = roleRepository.findByName("ROLE_USER")
//                .orElseThrow(() -> new RuntimeException("Role not found"));
//        user.setRoles(Set.of(userRole));
//
//        userRepository.save(user);
//        return "Registered successfully";
//    }
//}
