package org.example.colis.service;

import org.example.colis.dto.AssignColisRequest;
import org.example.colis.dto.ColisDTO;
import org.example.colis.dto.CreateColisRequest;
import org.example.colis.dto.UpdateColisRequest;
import org.example.colis.dto.UpdateStatutRequest;
import org.example.colis.enums.Role;
import org.example.colis.enums.Specialite;
import org.example.colis.enums.StatutColis;
import org.example.colis.enums.TypeColis;
import org.example.colis.exception.BusinessException;
import org.example.colis.exception.ResourceNotFoundException;
import org.example.colis.exception.UnauthorizedException;
import org.example.colis.mapper.ColisMapper;
import org.example.colis.model.Colis;
import org.example.colis.model.User;
import org.example.colis.repository.ColisRepository;
import org.example.colis.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ColisService {
    
    @Autowired
    private ColisRepository colisRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ColisMapper colisMapper;
    
    // Get all colis (ADMIN) or transporteur's colis (TRANSPORTEUR)
    public Page<ColisDTO> getAllColis(User currentUser, TypeColis type, StatutColis statut, Pageable pageable) {
        if (currentUser.getRole() == Role.ADMIN) {
            // Admin can see all colis
            if (type != null && statut != null) {
                return colisRepository.findByTypeAndStatut(type, statut, pageable).map(colisMapper::toDTO);
            } else if (type != null) {
                return colisRepository.findByType(type, pageable).map(colisMapper::toDTO);
            } else if (statut != null) {
                return colisRepository.findByStatut(statut, pageable).map(colisMapper::toDTO);
            } else {
                return colisRepository.findAll(pageable).map(colisMapper::toDTO);
            }
        } else {
            // Transporteur can only see their colis
            if (type != null && statut != null) {
                return colisRepository.findByTransporteurIdAndTypeAndStatut(currentUser.getId(), type, statut, pageable)
                        .map(colisMapper::toDTO);
            } else if (type != null) {
                return colisRepository.findByTransporteurIdAndType(currentUser.getId(), type, pageable)
                        .map(colisMapper::toDTO);
            } else if (statut != null) {
                return colisRepository.findByTransporteurIdAndStatut(currentUser.getId(), statut, pageable)
                        .map(colisMapper::toDTO);
            } else {
                return colisRepository.findByTransporteurId(currentUser.getId(), pageable)
                        .map(colisMapper::toDTO);
            }
        }
    }
    
    // Search colis by address
    public Page<ColisDTO> searchColisByAddress(User currentUser, String adresse, Pageable pageable) {
        if (currentUser.getRole() == Role.ADMIN) {
            return colisRepository.findByAdresseDestinationContainingIgnoreCase(adresse, pageable)
                    .map(colisMapper::toDTO);
        } else {
            return colisRepository.findByTransporteurIdAndAdresseDestinationContainingIgnoreCase(
                    currentUser.getId(), adresse, pageable
            ).map(colisMapper::toDTO);
        }
    }
    
    // Create new colis (ADMIN only)
    public ColisDTO createColis(CreateColisRequest request) {
        validateColisRequest(request);
        
        Colis colis = colisMapper.toEntity(request);
        Colis saved = colisRepository.save(colis);
        return colisMapper.toDTO(saved);
    }
    
    // Assign colis to transporteur (ADMIN only)
    public ColisDTO assignColis(String colisId, AssignColisRequest request) {
        Colis colis = colisRepository.findById(colisId)
                .orElseThrow(() -> new ResourceNotFoundException("Colis not found with id: " + colisId));
        
        User transporteur = userRepository.findById(request.getTransporteurId())
                .orElseThrow(() -> new ResourceNotFoundException("Transporteur not found with id: " + request.getTransporteurId()));
        
        // Validate transporteur role
        if (transporteur.getRole() != Role.TRANSPORTEUR) {
            throw new BusinessException("User is not a TRANSPORTEUR");
        }
        
        // Validate specialite matches type
        Specialite requiredSpecialite = mapTypeToSpecialite(colis.getType());
        if (transporteur.getSpecialite() != requiredSpecialite) {
            throw new BusinessException(
                    "Transporteur specialite (" + transporteur.getSpecialite() + 
                    ") does not match colis type (" + colis.getType() + ")"
            );
        }
        
        colis.setTransporteurId(transporteur.getId());
        Colis saved = colisRepository.save(colis);
        return colisMapper.toDTO(saved);
    }
    
    // Update colis (ADMIN only)
    public ColisDTO updateColis(String id, UpdateColisRequest request) {
        Colis colis = colisRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Colis not found with id: " + id));
        
        if (request.getType() != null) {
            colis.setType(request.getType());
        }
        if (request.getPoids() != null) {
            colis.setPoids(request.getPoids());
        }
        if (request.getAdresseDestination() != null) {
            colis.setAdresseDestination(request.getAdresseDestination());
        }
        if (request.getInstructionsManutention() != null) {
            colis.setInstructionsManutention(request.getInstructionsManutention());
        }
        if (request.getTemperatureMin() != null) {
            colis.setTemperatureMin(request.getTemperatureMin());
        }
        if (request.getTemperatureMax() != null) {
            colis.setTemperatureMax(request.getTemperatureMax());
        }
        
        Colis saved = colisRepository.save(colis);
        return colisMapper.toDTO(saved);
    }
    
    // Update colis statut (TRANSPORTEUR can update their colis, ADMIN can update all)
    public ColisDTO updateColisStatut(User currentUser, String id, UpdateStatutRequest request) {
        Colis colis = colisRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Colis not found with id: " + id));
        
        // Check authorization
        if (currentUser.getRole() == Role.TRANSPORTEUR) {
            if (colis.getTransporteurId() == null || !colis.getTransporteurId().equals(currentUser.getId())) {
                throw new UnauthorizedException("You can only update your own colis");
            }
        }
        
        colis.setStatut(request.getStatut());
        Colis saved = colisRepository.save(colis);
        return colisMapper.toDTO(saved);
    }
    
    // Delete colis (ADMIN only)
    public void deleteColis(String id) {
        Colis colis = colisRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Colis not found with id: " + id));
        colisRepository.delete(colis);
    }
    
    // Get colis by id
    public ColisDTO getColisById(User currentUser, String id) {
        Colis colis = colisRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Colis not found with id: " + id));
        
        // Check authorization for TRANSPORTEUR
        if (currentUser.getRole() == Role.TRANSPORTEUR) {
            if (colis.getTransporteurId() == null || !colis.getTransporteurId().equals(currentUser.getId())) {
                throw new UnauthorizedException("You can only view your own colis");
            }
        }
        
        return colisMapper.toDTO(colis);
    }
    
    // Helper methods
    private void validateColisRequest(CreateColisRequest request) {
        if (request.getType() == TypeColis.FRAGILE && request.getInstructionsManutention() == null) {
            throw new BusinessException("Instructions manutention is required for FRAGILE colis");
        }
        
        if (request.getType() == TypeColis.FRIGO) {
            if (request.getTemperatureMin() == null || request.getTemperatureMax() == null) {
                throw new BusinessException("Temperature min and max are required for FRIGO colis");
            }
            if (request.getTemperatureMin() >= request.getTemperatureMax()) {
                throw new BusinessException("Temperature min must be less than temperature max");
            }
        }
    }
    
    private Specialite mapTypeToSpecialite(TypeColis type) {
        return switch (type) {
            case STANDARD -> Specialite.STANDARD;
            case FRAGILE -> Specialite.FRAGILE;
            case FRIGO -> Specialite.FRIGO;
        };
    }
}
