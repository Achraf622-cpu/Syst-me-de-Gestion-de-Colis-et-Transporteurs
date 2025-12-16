package org.example.colis.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.colis.enums.StatutColis;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateStatutRequest {
    
    @NotNull(message = "Statut is required")
    private StatutColis statut;
}
