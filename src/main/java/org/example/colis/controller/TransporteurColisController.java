package org.example.colis.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.example.colis.dto.ColisDTO;
import org.example.colis.dto.UpdateStatutRequest;
import org.example.colis.enums.StatutColis;
import org.example.colis.enums.TypeColis;
import org.example.colis.model.User;
import org.example.colis.service.ColisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transporteur/colis")
@Tag(name = "Transporteur - Colis Management", description = "Transporteur endpoints for managing their colis")
@SecurityRequirement(name = "Bearer Authentication")
public class TransporteurColisController {
    
    @Autowired
    private ColisService colisService;
    
    @GetMapping
    @PreAuthorize("hasRole('TRANSPORTEUR')")
    @Operation(summary = "Get my colis", description = "Get paginated list of transporteur's colis with optional filters")
    public ResponseEntity<Page<ColisDTO>> getMyColis(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) TypeColis type,
            @RequestParam(required = false) StatutColis statut,
            @AuthenticationPrincipal User currentUser) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ColisDTO> colis = colisService.getAllColis(currentUser, type, statut, pageable);
        return ResponseEntity.ok(colis);
    }
    
    @GetMapping("/search")
    @PreAuthorize("hasRole('TRANSPORTEUR')")
    @Operation(summary = "Search my colis by address", description = "Search transporteur's colis by destination address")
    public ResponseEntity<Page<ColisDTO>> searchMyColisByAddress(
            @RequestParam String adresse,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal User currentUser) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ColisDTO> colis = colisService.searchColisByAddress(currentUser, adresse, pageable);
        return ResponseEntity.ok(colis);
    }
    
    @PatchMapping("/{id}/statut")
    @PreAuthorize("hasRole('TRANSPORTEUR')")
    @Operation(summary = "Update my colis statut", description = "Update the statut of transporteur's colis")
    public ResponseEntity<ColisDTO> updateMyColisStatut(
            @PathVariable String id,
            @Valid @RequestBody UpdateStatutRequest request,
            @AuthenticationPrincipal User currentUser) {
        ColisDTO updated = colisService.updateColisStatut(currentUser, id, request);
        return ResponseEntity.ok(updated);
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('TRANSPORTEUR')")
    @Operation(summary = "Get my colis by ID", description = "Get a specific colis by its ID")
    public ResponseEntity<ColisDTO> getMyColisById(
            @PathVariable String id,
            @AuthenticationPrincipal User currentUser) {
        ColisDTO colis = colisService.getColisById(currentUser, id);
        return ResponseEntity.ok(colis);
    }
}
