package org.example.colis.service;

import org.example.colis.dto.CreateUserRequest;
import org.example.colis.dto.UserDTO;
import org.example.colis.enums.Role;
import org.example.colis.enums.Specialite;
import org.example.colis.enums.StatutTransporteur;
import org.example.colis.exception.BusinessException;
import org.example.colis.exception.ResourceNotFoundException;
import org.example.colis.mapper.UserMapper;
import org.example.colis.model.User;
import org.example.colis.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private UserMapper userMapper;
    
    @Mock
    private PasswordEncoder passwordEncoder;
    
    @InjectMocks
    private UserService userService;
    
    private CreateUserRequest createTransporteurRequest;
    private User transporteur;
    private UserDTO transporteurDTO;
    
    @BeforeEach
    void setUp() {
        createTransporteurRequest = new CreateUserRequest();
        createTransporteurRequest.setLogin("transporteur1");
        createTransporteurRequest.setPassword("password123");
        createTransporteurRequest.setRole(Role.TRANSPORTEUR);
        createTransporteurRequest.setSpecialite(Specialite.STANDARD);
        createTransporteurRequest.setStatut(StatutTransporteur.DISPONIBLE);
        
        transporteur = new User();
        transporteur.setId("1");
        transporteur.setLogin("transporteur1");
        transporteur.setPassword("encodedPassword");
        transporteur.setRole(Role.TRANSPORTEUR);
        transporteur.setSpecialite(Specialite.STANDARD);
        transporteur.setStatut(StatutTransporteur.DISPONIBLE);
        transporteur.setActive(true);
        
        transporteurDTO = new UserDTO();
        transporteurDTO.setId("1");
        transporteurDTO.setLogin("transporteur1");
        transporteurDTO.setRole(Role.TRANSPORTEUR);
        transporteurDTO.setSpecialite(Specialite.STANDARD);
        transporteurDTO.setStatut(StatutTransporteur.DISPONIBLE);
        transporteurDTO.setActive(true);
    }
    
    @Test
    void createUser_WithValidTransporteur_ShouldCreateUser() {
        // Arrange
        when(userRepository.findByLogin("transporteur1")).thenReturn(Optional.empty());
        when(userMapper.toEntity(createTransporteurRequest)).thenReturn(transporteur);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(transporteur);
        when(userMapper.toDTO(transporteur)).thenReturn(transporteurDTO);
        
        // Act
        UserDTO result = userService.createUser(createTransporteurRequest);
        
        // Assert
        assertNotNull(result);
        assertEquals("transporteur1", result.getLogin());
        assertEquals(Role.TRANSPORTEUR, result.getRole());
        assertEquals(Specialite.STANDARD, result.getSpecialite());
        
        verify(userRepository).findByLogin("transporteur1");
        verify(passwordEncoder).encode("password123");
        verify(userRepository).save(any(User.class));
    }
    
    @Test
    void createUser_TransporteurWithoutSpecialite_ShouldThrowBusinessException() {
        // Arrange
        createTransporteurRequest.setSpecialite(null);
        when(userRepository.findByLogin("transporteur1")).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThrows(BusinessException.class, () -> userService.createUser(createTransporteurRequest));
        
        verify(userRepository).findByLogin("transporteur1");
        verify(userRepository, never()).save(any());
    }
    
    @Test
    void createUser_TransporteurWithoutStatut_ShouldThrowBusinessException() {
        // Arrange
        createTransporteurRequest.setStatut(null);
        when(userRepository.findByLogin("transporteur1")).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThrows(BusinessException.class, () -> userService.createUser(createTransporteurRequest));
        
        verify(userRepository).findByLogin("transporteur1");
        verify(userRepository, never()).save(any());
    }
    
    @Test
    void createUser_AdminWithSpecialite_ShouldThrowBusinessException() {
        // Arrange
        CreateUserRequest adminRequest = new CreateUserRequest();
        adminRequest.setLogin("admin1");
        adminRequest.setPassword("password");
        adminRequest.setRole(Role.ADMIN);
        adminRequest.setSpecialite(Specialite.STANDARD); // ADMIN should not have specialite
        
        when(userRepository.findByLogin("admin1")).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThrows(BusinessException.class, () -> userService.createUser(adminRequest));
        
        verify(userRepository).findByLogin("admin1");
        verify(userRepository, never()).save(any());
    }
    
    @Test
    void createUser_WithExistingLogin_ShouldThrowBusinessException() {
        // Arrange
        when(userRepository.findByLogin("transporteur1")).thenReturn(Optional.of(transporteur));
        
        // Act & Assert
        assertThrows(BusinessException.class, () -> userService.createUser(createTransporteurRequest));
        
        verify(userRepository).findByLogin("transporteur1");
        verify(userRepository, never()).save(any());
    }
    
    @Test
    void deleteUser_WithExistingUser_ShouldDeleteUser() {
        // Arrange
        when(userRepository.findById("1")).thenReturn(Optional.of(transporteur));
        
        // Act
        userService.deleteUser("1");
        
        // Assert
        verify(userRepository).findById("1");
        verify(userRepository).delete(transporteur);
    }
    
    @Test
    void deleteUser_WithNonExistentUser_ShouldThrowResourceNotFoundException() {
        // Arrange
        when(userRepository.findById("999")).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> userService.deleteUser("999"));
        
        verify(userRepository).findById("999");
        verify(userRepository, never()).delete(any());
    }
    
    @Test
    void getUserById_WithExistingUser_ShouldReturnUserDTO() {
        // Arrange
        when(userRepository.findById("1")).thenReturn(Optional.of(transporteur));
        when(userMapper.toDTO(transporteur)).thenReturn(transporteurDTO);
        
        // Act
        UserDTO result = userService.getUserById("1");
        
        // Assert
        assertNotNull(result);
        assertEquals("1", result.getId());
        assertEquals("transporteur1", result.getLogin());
        
        verify(userRepository).findById("1");
        verify(userMapper).toDTO(transporteur);
    }
}
