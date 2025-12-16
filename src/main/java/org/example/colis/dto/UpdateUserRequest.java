package org.example.colis.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.colis.enums.Specialite;
import org.example.colis.enums.StatutTransporteur;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRequest {
    
    private String login;
    
    private String password;
    
    private Boolean active;
    
    // Fields specific to TRANSPORTEUR
    private StatutTransporteur statut;
    
    private Specialite specialite;
}
