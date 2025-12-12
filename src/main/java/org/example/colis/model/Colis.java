package org.example.colis.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.colis.enums.StatutColis;
import org.example.colis.enums.TypeColis;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "colis")
public class Colis {
    
    @Id
    private String id;
    
    private TypeColis type;
    
    private Double poids;
    
    private String adresseDestination;
    
    private StatutColis statut = StatutColis.EN_ATTENTE;
    
    private String transporteurId;
    
    // Specific to FRAGILE
    private String instructionsManutention;
    
    // Specific to FRIGO
    private Double temperatureMin;
    
    private Double temperatureMax;
}
