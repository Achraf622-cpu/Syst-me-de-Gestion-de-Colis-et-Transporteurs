package org.example.colis.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.colis.enums.StatutColis;
import org.example.colis.enums.TypeColis;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ColisDTO {
    
    private String id;
    
    private TypeColis type;
    
    private Double poids;
    
    private String adresseDestination;
    
    private StatutColis statut;
    
    private String transporteurId;
    
    // Specific to FRAGILE
    private String instructionsManutention;
    
    // Specific to FRIGO
    private Double temperatureMin;
    
    private Double temperatureMax;
}
