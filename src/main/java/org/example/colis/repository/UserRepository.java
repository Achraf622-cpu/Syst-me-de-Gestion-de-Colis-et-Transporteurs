package org.example.colis.repository;

import org.example.colis.enums.Role;
import org.example.colis.enums.Specialite;
import org.example.colis.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    
    Optional<User> findByLogin(String login);
    
    Optional<User> findByLoginAndActiveTrue(String login);
    
    Page<User> findByRole(Role role, Pageable pageable);
    
    Page<User> findByRoleAndSpecialite(Role role, Specialite specialite, Pageable pageable);
}
