package org.example.colis.service;

import org.example.colis.dto.CreateUserRequest;
import org.example.colis.dto.UpdateUserRequest;
import org.example.colis.dto.UserDTO;
import org.example.colis.enums.Role;
import org.example.colis.enums.Specialite;
import org.example.colis.exception.BusinessException;
import org.example.colis.exception.ResourceNotFoundException;
import org.example.colis.mapper.UserMapper;
import org.example.colis.model.User;
import org.example.colis.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    public Page<UserDTO> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(userMapper::toDTO);
    }
    
    public Page<UserDTO> getAllTransporteurs(Pageable pageable) {
        return userRepository.findByRole(Role.TRANSPORTEUR, pageable)
                .map(userMapper::toDTO);
    }
    
    public Page<UserDTO> getTransporteursBySpecialite(Specialite specialite, Pageable pageable) {
        return userRepository.findByRoleAndSpecialite(Role.TRANSPORTEUR, specialite, pageable)
                .map(userMapper::toDTO);
    }
    
    public UserDTO createUser(CreateUserRequest request) {
        // Check if login already exists
        Optional<User> existing = userRepository.findByLogin(request.getLogin());
        if (existing.isPresent()) {
            throw new BusinessException("Login already exists");
        }
        
        // Validate TRANSPORTEUR specific fields
        if (request.getRole() == Role.TRANSPORTEUR) {
            if (request.getSpecialite() == null) {
                throw new BusinessException("Specialite is required for TRANSPORTEUR");
            }
            if (request.getStatut() == null) {
                throw new BusinessException("Statut is required for TRANSPORTEUR");
            }
        } else if (request.getRole() == Role.ADMIN) {
            // ADMIN should not have TRANSPORTEUR fields
            if (request.getSpecialite() != null || request.getStatut() != null) {
                throw new BusinessException("ADMIN cannot have specialite or statut");
            }
        }
        
        User user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setActive(true);
        
        User saved = userRepository.save(user);
        return userMapper.toDTO(saved);
    }
    
    public UserDTO updateUser(String id, UpdateUserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        
        // Check if new login already exists
        if (request.getLogin() != null && !request.getLogin().equals(user.getLogin())) {
            Optional<User> existing = userRepository.findByLogin(request.getLogin());
            if (existing.isPresent()) {
                throw new BusinessException("Login already exists");
            }
            user.setLogin(request.getLogin());
        }
        
        if (request.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        
        if (request.getActive() != null) {
            user.setActive(request.getActive());
        }
        
        // Update TRANSPORTEUR specific fields
        if (user.getRole() == Role.TRANSPORTEUR) {
            if (request.getStatut() != null) {
                user.setStatut(request.getStatut());
            }
            if (request.getSpecialite() != null) {
                user.setSpecialite(request.getSpecialite());
            }
        }
        
        User saved = userRepository.save(user);
        return userMapper.toDTO(saved);
    }
    
    public void deleteUser(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        userRepository.delete(user);
    }
    
    public UserDTO getUserById(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return userMapper.toDTO(user);
    }
}
