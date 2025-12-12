package org.example.colis.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.colis.enums.Role;
import org.example.colis.enums.Specialite;
import org.example.colis.enums.StatutTransporteur;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "users")
public class User {
    
    @Id
    private String id;
    
    private String login;
    
    private String password;
    
    private Role role;
    
    private Boolean active = true;
    
    // Fields specific to TRANSPORTEUR
    private StatutTransporteur statut;
    
    private Specialite specialite;
}
