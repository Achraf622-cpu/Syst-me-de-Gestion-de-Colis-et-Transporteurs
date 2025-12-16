package org.example.colis.mapper;

import org.example.colis.dto.ColisDTO;
import org.example.colis.dto.CreateColisRequest;
import org.example.colis.enums.StatutColis;
import org.example.colis.model.Colis;
import org.springframework.stereotype.Component;

@Component
public class ColisMapper {
    
    public ColisDTO toDTO(Colis colis) {
        if (colis == null) {
            return null;
        }
        
        ColisDTO dto = new ColisDTO();
        dto.setId(colis.getId());
        dto.setType(colis.getType());
        dto.setPoids(colis.getPoids());
        dto.setAdresseDestination(colis.getAdresseDestination());
        dto.setStatut(colis.getStatut());
        dto.setTransporteurId(colis.getTransporteurId());
        dto.setInstructionsManutention(colis.getInstructionsManutention());
        dto.setTemperatureMin(colis.getTemperatureMin());
        dto.setTemperatureMax(colis.getTemperatureMax());
        
        return dto;
    }
    
    public Colis toEntity(CreateColisRequest request) {
        if (request == null) {
            return null;
        }
        
        Colis colis = new Colis();
        colis.setType(request.getType());
        colis.setPoids(request.getPoids());
        colis.setAdresseDestination(request.getAdresseDestination());
        colis.setStatut(StatutColis.EN_ATTENTE);
        colis.setInstructionsManutention(request.getInstructionsManutention());
        colis.setTemperatureMin(request.getTemperatureMin());
        colis.setTemperatureMax(request.getTemperatureMax());
        
        return colis;
    }
}
