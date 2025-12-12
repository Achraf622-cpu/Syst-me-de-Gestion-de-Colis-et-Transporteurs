package org.example.colis.repository;

import org.example.colis.enums.StatutColis;
import org.example.colis.enums.TypeColis;
import org.example.colis.model.Colis;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ColisRepository extends MongoRepository<Colis, String> {
    
    Page<Colis> findByType(TypeColis type, Pageable pageable);
    
    Page<Colis> findByStatut(StatutColis statut, Pageable pageable);
    
    Page<Colis> findByTypeAndStatut(TypeColis type, StatutColis statut, Pageable pageable);
    
    Page<Colis> findByAdresseDestinationContainingIgnoreCase(String adresse, Pageable pageable);
    
    Page<Colis> findByTransporteurId(String transporteurId, Pageable pageable);
    
    Page<Colis> findByTransporteurIdAndType(String transporteurId, TypeColis type, Pageable pageable);
    
    Page<Colis> findByTransporteurIdAndStatut(String transporteurId, StatutColis statut, Pageable pageable);
    
    Page<Colis> findByTransporteurIdAndTypeAndStatut(String transporteurId, TypeColis type, StatutColis statut, Pageable pageable);
    
    Page<Colis> findByTransporteurIdAndAdresseDestinationContainingIgnoreCase(String transporteurId, String adresse, Pageable pageable);
}
