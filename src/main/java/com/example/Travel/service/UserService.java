package com.example.Travel.service;

import com.example.Travel.dto.UserDTO;
import com.example.Travel.dto.UserRegistrationDTO;
import com.example.Travel.entity.Role;
import com.example.Travel.entity.User;
import com.example.Travel.repository.RoleRepository;
import com.example.Travel.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Create new user (registration)
    public UserDTO createUser(UserRegistrationDTO registrationDTO) {
        if (userRepository.existsByEmail(registrationDTO.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        if (userRepository.existsByUsername(registrationDTO.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

        User user = new User();
        user.setUsername(registrationDTO.getUsername());
        user.setFirstName(registrationDTO.getFirstName());
        user.setLastName(registrationDTO.getLastName());
        user.setEmail(registrationDTO.getEmail());
        user.setPassword(passwordEncoder.encode(registrationDTO.getPassword()));
        user.setPhone(registrationDTO.getPhone());

        // Set default role as USER
        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseGet(() -> {
                    Role newRole = new Role();
                    newRole.setName("ROLE_USER");
                    newRole.setDescription("Default user role");
                    return roleRepository.save(newRole);
                });

        Set<Role> roles = new HashSet<>();
        roles.add(userRole);
        user.setRoles(roles);

        User savedUser = userRepository.save(user);
        return convertToDTO(savedUser);
    }

    // Get user by ID
    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return convertToDTO(user);
    }

    // Get user by username
    public UserDTO getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return convertToDTO(user);
    }

    // Get current logged-in user
    public UserDTO getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        return getUserByUsername(username);
    }

    // Update user profile
    public UserDTO updateUser(Long id, UserRegistrationDTO updateDTO) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Update fields
        if (updateDTO.getFirstName() != null) {
            user.setFirstName(updateDTO.getFirstName());
        }
        if (updateDTO.getLastName() != null) {
            user.setLastName(updateDTO.getLastName());
        }
        if (updateDTO.getEmail() != null && !updateDTO.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(updateDTO.getEmail())) {
                throw new RuntimeException("Email already exists");
            }
            user.setEmail(updateDTO.getEmail());
        }
        if (updateDTO.getPhone() != null) {
            user.setPhone(updateDTO.getPhone());
        }
        if (updateDTO.getPassword() != null && !updateDTO.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(updateDTO.getPassword()));
        }

        User updatedUser = userRepository.save(user);
        return convertToDTO(updatedUser);
    }

    // Update user by username (for profile updates)
    public UserDTO updateUserByUsername(String username, UserRegistrationDTO updateDTO) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return updateUser(user.getId(), updateDTO);
    }

    // Delete user
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found");
        }
        userRepository.deleteById(id);
    }

    // Get all users (for admin)
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Update user role (admin only)
    public UserDTO updateUserRole(Long userId, String roleName) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new RuntimeException("Role not found: " + roleName));

        Set<Role> roles = new HashSet<>();
        roles.add(role);
        user.setRoles(roles);

        User updatedUser = userRepository.save(user);
        return convertToDTO(updatedUser);
    }

    // Update user status (enable/disable)
    public UserDTO updateUserStatus(Long userId, boolean enabled) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setEnabled(enabled);
        User updatedUser = userRepository.save(user);
        return convertToDTO(updatedUser);
    }

    // Check if username exists
    public boolean usernameExists(String username) {
        return userRepository.existsByUsername(username);
    }

    // Check if email exists
    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    // Convert User entity to UserDTO
    private UserDTO convertToDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        dto.setEnabled(user.getEnabled());

        // Get user role (first role in set)
        Optional<String> roleName = user.getRoles().stream()
                .map(Role::getName)
                .findFirst();
        roleName.ifPresent(dto::setRole);

        return dto;
    }

    // In UserService, add method to promote user to admin
    public UserDTO assignAdminRole(Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        Role adminRole = roleRepository.findByName("ROLE_ADMIN").orElseThrow();
        user.getRoles().add(adminRole);
        return convertToDTO(userRepository.save(user));
    }

    public UserDTO promoteToAdmin(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Role adminRole = roleRepository.findByName("ROLE_ADMIN")
                .orElseThrow(() -> new RuntimeException("Admin role not found"));
        user.getRoles().add(adminRole);
        return convertToDTO(userRepository.save(user));
    }

    // Convert UserRegistrationDTO to User entity
    private User convertToEntity(UserRegistrationDTO dto) {
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setPhone(dto.getPhone());
        user.setEnabled(true);
        user.setAccountNonExpired(true);
        user.setCredentialsNonExpired(true);
        user.setAccountNonLocked(true);

        // Assign default USER role
        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseGet(() -> {
                    Role role = new Role();
                    role.setName("ROLE_USER");
                    role.setDescription("User role");
                    return roleRepository.save(role);
                });

        Set<Role> roles = new HashSet<>();
        roles.add(userRole);
        user.setRoles(roles);

        return user;
    }
    public User getUserEntityByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));    }
}

//package com.example.Travel.service;
//
//import com.example.Travel.dto.UserDTO;
//import com.example.Travel.dto.UserRegistrationDTO;
//import com.example.Travel.entity.Role;
//import com.example.Travel.entity.User;
//import com.example.Travel.repository.RoleRepository;
//import com.example.Travel.repository.UserRepository;
//import jakarta.transaction.Transactional;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Service;
//
//import java.util.HashSet;
//import java.util.List;
//import java.util.Set;
//import java.util.stream.Collectors;
//
//@Service
//@Transactional
//public class UserService {
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
//    public UserDTO createUser(UserRegistrationDTO registrationDTO) {
//        if (userRepository.existsByEmail(registrationDTO.getEmail())) {
//            throw new RuntimeException("Email already exists");
//        }
//        if (userRepository.existsByUsername(registrationDTO.getUsername())) {
//            throw new RuntimeException("Username already exists");
//        }
//
//        User user = new User();
//        user.setUsername(registrationDTO.getUsername());
//        user.setFirstName(registrationDTO.getFirstName());
//        user.setLastName(registrationDTO.getLastName());
//        user.setEmail(registrationDTO.getEmail());
//        user.setPassword(passwordEncoder.encode(registrationDTO.getPassword()));
//        user.setPhone(registrationDTO.getPhone());
//        user.setEnabled(true);
//        user.setAccountNonExpired(true);
//        user.setCredentialsNonExpired(true);
//        user.setAccountNonLocked(true);
//
//        // Set default role
//        Role userRole = roleRepository.findByName("ROLE_USER")
//                .orElseGet(() -> {
//                    Role newRole = new Role();
//                    newRole.setName("ROLE_USER");
//                    newRole.setDescription("Default user role");
//                    return roleRepository.save(newRole);
//                });
//
//        Set<Role> roles = new HashSet<>();
//        roles.add(userRole);
//        user.setRoles(roles);
//
//        User savedUser = userRepository.save(user);
//        return convertToDTO(savedUser);
//    }
//
//    public UserDTO getUserById(Long id) {
//        User user = userRepository.findById(id)
//                .orElseThrow(() -> new RuntimeException("User not found"));
//        return convertToDTO(user);
//    }
//
//    public UserDTO getUserByUsername(String username) {
//        User user = userRepository.findByUsername(username)
//                .orElseThrow(() -> new RuntimeException("User not found: " + username));
//        return convertToDTO(user);
//    }
//
//    public UserDTO updateUser(Long id, UserRegistrationDTO updateDTO) {
//        User user = userRepository.findById(id)
//                .orElseThrow(() -> new RuntimeException("User not found"));
//
//        // Check if email is being changed and if new email already exists
//        if (!user.getEmail().equals(updateDTO.getEmail()) &&
//                userRepository.existsByEmail(updateDTO.getEmail())) {
//            throw new RuntimeException("Email already exists");
//        }
//
//        user.setFirstName(updateDTO.getFirstName());
//        user.setLastName(updateDTO.getLastName());
//        user.setEmail(updateDTO.getEmail());
//        user.setPhone(updateDTO.getPhone());
//
//        User updatedUser = userRepository.save(user);
//        return convertToDTO(updatedUser);
//    }
//
//    public UserDTO updateUserByUsername(String username, UserRegistrationDTO updateDTO) {
//        User user = userRepository.findByUsername(username)
//                .orElseThrow(() -> new RuntimeException("User not found"));
//
//        // Check if email is being changed and if new email already exists
//        if (!user.getEmail().equals(updateDTO.getEmail()) &&
//                userRepository.existsByEmail(updateDTO.getEmail())) {
//            throw new RuntimeException("Email already exists");
//        }
//
//        user.setFirstName(updateDTO.getFirstName());
//        user.setLastName(updateDTO.getLastName());
//        user.setEmail(updateDTO.getEmail());
//        user.setPhone(updateDTO.getPhone());
//
//        // Allow password update if provided
//        if (updateDTO.getPassword() != null && !updateDTO.getPassword().trim().isEmpty()) {
//            user.setPassword(passwordEncoder.encode(updateDTO.getPassword()));
//        }
//
//        User updatedUser = userRepository.save(user);
//        return convertToDTO(updatedUser);
//    }
//
//    public void deleteUser(Long id) {
//        userRepository.deleteById(id);
//    }
//
//    public List<UserDTO> getAllUsers() {
//        return userRepository.findAll().stream()
//                .map(this::convertToDTO)
//                .collect(Collectors.toList());
//    }
//
//    public boolean usernameExists(String username) {
//        return userRepository.existsByUsername(username);
//    }
//
//    public boolean emailExists(String email) {
//        return userRepository.existsByEmail(email);
//    }
//
//    public List<UserDTO> getUsersByRole(String roleName) {
//        Role role = roleRepository.findByName(roleName)
//                .orElseThrow(() -> new RuntimeException("Role not found: " + roleName));
//
//        return userRepository.findAll().stream()
//                .filter(user -> user.getRoles().contains(role))
//                .map(this::convertToDTO)
//                .collect(Collectors.toList());
//    }
//
//    public UserDTO updateUserRole(Long userId, String roleName) {
//        User user = userRepository.findById(userId)
//                .orElseThrow(() -> new RuntimeException("User not found"));
//
//        Role role = roleRepository.findByName(roleName)
//                .orElseThrow(() -> new RuntimeException("Role not found: " + roleName));
//
//        Set<Role> roles = new HashSet<>();
//        roles.add(role);
//        user.setRoles(roles);
//
//        User updatedUser = userRepository.save(user);
//        return convertToDTO(updatedUser);
//    }
//
//    private UserDTO convertToDTO(User user) {
//        UserDTO dto = new UserDTO();
//        dto.setId(user.getId());
//        dto.setUsername(user.getUsername());
//        dto.setFirstName(user.getFirstName());
//        dto.setLastName(user.getLastName());
//        dto.setEmail(user.getEmail());
//        dto.setPhone(user.getPhone());
//        dto.setEnabled(user.getEnabled());
//
//        // Add roles to DTO
//        if (user.getRoles() != null) {
//            String role = user.getRoles().stream()
//                    .map(Role::getName)
//                    .findFirst()
//                    .orElse("ROLE_USER");
//            dto.setRole(role);
//        }
//
//        return dto;
//    }
//}