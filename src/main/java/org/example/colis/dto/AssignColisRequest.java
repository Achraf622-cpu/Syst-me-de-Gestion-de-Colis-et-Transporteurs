package org.example.colis.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssignColisRequest {
    
    @NotBlank(message = "Transporteur ID is required")
    private String transporteurId;
}
