package org.example.colis.service;

import org.example.colis.dto.AssignColisRequest;
import org.example.colis.dto.ColisDTO;
import org.example.colis.dto.CreateColisRequest;
import org.example.colis.enums.Role;
import org.example.colis.enums.Specialite;
import org.example.colis.enums.StatutColis;
import org.example.colis.enums.TypeColis;
import org.example.colis.exception.BusinessException;
import org.example.colis.exception.ResourceNotFoundException;
import org.example.colis.mapper.ColisMapper;
import org.example.colis.model.Colis;
import org.example.colis.model.User;
import org.example.colis.repository.ColisRepository;
import org.example.colis.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ColisServiceTest {
    
    @Mock
    private ColisRepository colisRepository;
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private ColisMapper colisMapper;
    
    @InjectMocks
    private ColisService colisService;
    
    private CreateColisRequest createColisRequest;
    private Colis colis;
    private ColisDTO colisDTO;
    private User transporteur;
    
    @BeforeEach
    void setUp() {
        // Setup create request for FRAGILE colis
        createColisRequest = new CreateColisRequest();
        createColisRequest.setType(TypeColis.FRAGILE);
        createColisRequest.setPoids(5.0);
        createColisRequest.setAdresseDestination("123 Rue Test");
        createColisRequest.setInstructionsManutention("Handle with care");
        
        // Setup colis entity
        colis = new Colis();
        colis.setId("1");
        colis.setType(TypeColis.FRAGILE);
        colis.setPoids(5.0);
        colis.setAdresseDestination("123 Rue Test");
        colis.setStatut(StatutColis.EN_ATTENTE);
        colis.setInstructionsManutention("Handle with care");
        
        // Setup colis DTO
        colisDTO = new ColisDTO();
        colisDTO.setId("1");
        colisDTO.setType(TypeColis.FRAGILE);
        colisDTO.setPoids(5.0);
        colisDTO.setAdresseDestination("123 Rue Test");
        colisDTO.setStatut(StatutColis.EN_ATTENTE);
        
        // Setup transporteur
        transporteur = new User();
        transporteur.setId("trans1");
        transporteur.setLogin("transporteur_fragile");
        transporteur.setRole(Role.TRANSPORTEUR);
        transporteur.setSpecialite(Specialite.FRAGILE);
        transporteur.setActive(true);
    }
    
    @Test
    void createColis_WithValidFragileData_ShouldCreateColis() {
        // Arrange
        when(colisMapper.toEntity(createColisRequest)).thenReturn(colis);
        when(colisRepository.save(colis)).thenReturn(colis);
        when(colisMapper.toDTO(colis)).thenReturn(colisDTO);
        
        // Act
        ColisDTO result = colisService.createColis(createColisRequest);
        
        // Assert
        assertNotNull(result);
        assertEquals(TypeColis.FRAGILE, result.getType());
        assertEquals(5.0, result.getPoids());
        
        verify(colisMapper).toEntity(createColisRequest);
        verify(colisRepository).save(colis);
        verify(colisMapper).toDTO(colis);
    }
    
    @Test
    void createColis_FragileWithoutInstructions_ShouldThrowBusinessException() {
        // Arrange
        createColisRequest.setInstructionsManutention(null);
        
        // Act & Assert
        assertThrows(BusinessException.class, () -> colisService.createColis(createColisRequest));
        
        verify(colisRepository, never()).save(any());
    }
    
    @Test
    void createColis_FrigoWithInvalidTemperature_ShouldThrowBusinessException() {
        // Arrange
        CreateColisRequest frigoRequest = new CreateColisRequest();
        frigoRequest.setType(TypeColis.FRIGO);
        frigoRequest.setPoids(10.0);
        frigoRequest.setAdresseDestination("456 Rue Test");
        frigoRequest.setTemperatureMin(10.0);
        frigoRequest.setTemperatureMax(5.0); // Invalid: min > max
        
        // Act & Assert
        assertThrows(BusinessException.class, () -> colisService.createColis(frigoRequest));
        
        verify(colisRepository, never()).save(any());
    }
    
    @Test
    void assignColis_WithMatchingSpecialite_ShouldAssignColis() {
        // Arrange
        AssignColisRequest request = new AssignColisRequest("trans1");
        when(colisRepository.findById("1")).thenReturn(Optional.of(colis));
        when(userRepository.findById("trans1")).thenReturn(Optional.of(transporteur));
        when(colisRepository.save(colis)).thenReturn(colis);
        when(colisMapper.toDTO(colis)).thenReturn(colisDTO);
        
        // Act
        ColisDTO result = colisService.assignColis("1", request);
        
        // Assert
        assertNotNull(result);
        verify(colisRepository).findById("1");
        verify(userRepository).findById("trans1");
        verify(colisRepository).save(colis);
    }
    
    @Test
    void assignColis_WithMismatchedSpecialite_ShouldThrowBusinessException() {
        // Arrange
        AssignColisRequest request = new AssignColisRequest("trans1");
        transporteur.setSpecialite(Specialite.STANDARD); // Mismatch with FRAGILE colis
        
        when(colisRepository.findById("1")).thenReturn(Optional.of(colis));
        when(userRepository.findById("trans1")).thenReturn(Optional.of(transporteur));
        
        // Act & Assert
        assertThrows(BusinessException.class, () -> colisService.assignColis("1", request));
        
        verify(colisRepository, never()).save(any());
    }
    
    @Test
    void assignColis_WithNonExistentColis_ShouldThrowResourceNotFoundException() {
        // Arrange
        AssignColisRequest request = new AssignColisRequest("trans1");
        when(colisRepository.findById("999")).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> colisService.assignColis("999", request));
        
        verify(colisRepository).findById("999");
        verify(userRepository, never()).findById(any());
    }
    
    @Test
    void assignColis_WithNonTransporteurUser_ShouldThrowBusinessException() {
        // Arrange
        AssignColisRequest request = new AssignColisRequest("admin1");
        User admin = new User();
        admin.setId("admin1");
        admin.setRole(Role.ADMIN);
        
        when(colisRepository.findById("1")).thenReturn(Optional.of(colis));
        when(userRepository.findById("admin1")).thenReturn(Optional.of(admin));
        
        // Act & Assert
        assertThrows(BusinessException.class, () -> colisService.assignColis("1", request));
        
        verify(colisRepository, never()).save(any());
    }
    
    @Test
    void deleteColis_WithExistingColis_ShouldDeleteColis() {
        // Arrange
        when(colisRepository.findById("1")).thenReturn(Optional.of(colis));
        
        // Act
        colisService.deleteColis("1");
        
        // Assert
        verify(colisRepository).findById("1");
        verify(colisRepository).delete(colis);
    }
    
    @Test
    void deleteColis_WithNonExistentColis_ShouldThrowResourceNotFoundException() {
        // Arrange
        when(colisRepository.findById("999")).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> colisService.deleteColis("999"));
        
        verify(colisRepository).findById("999");
        verify(colisRepository, never()).delete(any());
    }
}
