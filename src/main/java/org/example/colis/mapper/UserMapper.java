package org.example.colis.mapper;

import org.example.colis.dto.CreateUserRequest;
import org.example.colis.dto.UserDTO;
import org.example.colis.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    
    public UserDTO toDTO(User user) {
        if (user == null) {
            return null;
        }
        
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setLogin(user.getLogin());
        dto.setRole(user.getRole());
        dto.setActive(user.getActive());
        dto.setStatut(user.getStatut());
        dto.setSpecialite(user.getSpecialite());
        
        return dto;
    }
    
    public User toEntity(CreateUserRequest request) {
        if (request == null) {
            return null;
        }
        
        User user = new User();
        user.setLogin(request.getLogin());
        user.setPassword(request.getPassword());
        user.setRole(request.getRole());
        user.setActive(true);
        user.setStatut(request.getStatut());
        user.setSpecialite(request.getSpecialite());
        
        return user;
    }
}
