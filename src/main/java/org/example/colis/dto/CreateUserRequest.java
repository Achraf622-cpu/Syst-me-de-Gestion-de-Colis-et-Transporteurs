package org.example.colis.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.colis.enums.Role;
import org.example.colis.enums.Specialite;
import org.example.colis.enums.StatutTransporteur;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserRequest {
    
    @NotBlank(message = "Login is required")
    private String login;
    
    @NotBlank(message = "Password is required")
    private String password;
    
    @NotNull(message = "Role is required")
    private Role role;
    
    // Fields specific to TRANSPORTEUR (validated in service)
    private StatutTransporteur statut;
    
    private Specialite specialite;
}
