package org.example.colis.dto;

import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.colis.enums.TypeColis;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateColisRequest {
    
    private TypeColis type;
    
    @Positive(message = "Poids must be positive")
    private Double poids;
    
    private String adresseDestination;
    
    // Specific to FRAGILE
    private String instructionsManutention;
    
    // Specific to FRIGO
    private Double temperatureMin;
    
    private Double temperatureMax;
}
