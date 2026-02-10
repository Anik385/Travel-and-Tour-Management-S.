package com.example.Travel.service;

import com.example.Travel.entity.Role;
import com.example.Travel.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;

    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    public Role getRoleByName(String name) {
        return roleRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("Role not found: " + name));
    }

    public Role createRole(Role role) {
        if (roleRepository.existsByName(role.getName())) {
            throw new RuntimeException("Role already exists: " + role.getName());
        }
        return roleRepository.save(role);
    }

    public void deleteRole(String name) {
        roleRepository.deleteById(name);
    }

    @Transactional
    public void initializeDefaultRoles() {
        String[] defaultRoles = {"ROLE_USER", "ROLE_ADMIN", "ROLE_MODERATOR"};
        for (String roleName : defaultRoles) {
            if (!roleRepository.existsByName(roleName)) {
                Role role = new Role();
                role.setName(roleName);
                role.setDescription(roleName.replace("ROLE_", "") + " Role");
                roleRepository.save(role);
                System.out.println("✅ Created role: " + roleName);
            }
        }
        System.out.println("✅ Default roles initialized");
    }
}

//package com.example.Travel.service;
//
//import com.example.Travel.entity.Role;
//import com.example.Travel.repository.RoleRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import java.util.List;
//
//@Service
//public class RoleService {
//
//    @Autowired
//    private RoleRepository roleRepository;
//
//    public List<Role> getAllRoles() {
//        return roleRepository.findAll();
//    }
//
//    public Role getRoleByName(String name) {
//        return roleRepository.findByName(name)
//                .orElseThrow(() -> new RuntimeException("Role not found: " + name));
//    }
//
//    public Role createRole(Role role) {
//        if (roleRepository.existsByName(role.getName())) {
//            throw new RuntimeException("Role already exists: " + role.getName());
//        }
//        return roleRepository.save(role);
//    }
//
//    public void deleteRole(String name) {
//        roleRepository.deleteById(name);
//    }
//
//    public void initializeDefaultRoles() {
//        String[] defaultRoles = {"ROLE_USER", "ROLE_ADMIN", "ROLE_MODERATOR"};
//        for (String roleName : defaultRoles) {
//            if (!roleRepository.existsByName(roleName)) {
//                Role role = new Role();
//                role.setName(roleName);
//                role.setDescription(roleName.replace("ROLE_", "") + " Role");
//                roleRepository.save(role);
//            }
//        }
//    }
//}
