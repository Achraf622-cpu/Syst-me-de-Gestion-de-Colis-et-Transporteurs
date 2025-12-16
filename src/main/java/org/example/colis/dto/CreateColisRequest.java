package org.example.colis.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.colis.enums.TypeColis;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateColisRequest {
    
    @NotNull(message = "Type is required")
    private TypeColis type;
    
    @NotNull(message = "Poids is required")
    @Positive(message = "Poids must be positive")
    private Double poids;
    
    @NotBlank(message = "Adresse destination is required")
    private String adresseDestination;
    
    // Specific to FRAGILE
    private String instructionsManutention;
    
    // Specific to FRIGO
    private Double temperatureMin;
    
    private Double temperatureMax;
}
