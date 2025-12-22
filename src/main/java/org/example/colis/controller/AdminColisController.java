package org.example.colis.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.example.colis.dto.AssignColisRequest;
import org.example.colis.dto.ColisDTO;
import org.example.colis.dto.CreateColisRequest;
import org.example.colis.dto.PageResponse;
import org.example.colis.dto.UpdateColisRequest;
import org.example.colis.dto.UpdateStatutRequest;
import org.example.colis.enums.StatutColis;
import org.example.colis.enums.TypeColis;
import org.example.colis.model.User;
import org.example.colis.service.ColisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/colis")
@Tag(name = "Admin - Colis Management", description = "Admin endpoints for managing colis")
@SecurityRequirement(name = "Bearer Authentication")
public class AdminColisController {

    @Autowired
    private ColisService colisService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all colis", description = "Get paginated list of all colis with optional filters")
    public ResponseEntity<PageResponse<ColisDTO>> getAllColis(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) TypeColis type,
            @RequestParam(required = false) StatutColis statut,
            @AuthenticationPrincipal User currentUser) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(PageResponse.from(colisService.getAllColis(currentUser, type, statut, pageable)));
    }

    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Search colis by address", description = "Search colis by destination address")
    public ResponseEntity<PageResponse<ColisDTO>> searchColisByAddress(
            @RequestParam String adresse,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal User currentUser) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(PageResponse.from(colisService.searchColisByAddress(currentUser, adresse, pageable)));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create colis", description = "Create a new colis")
    public ResponseEntity<ColisDTO> createColis(@Valid @RequestBody CreateColisRequest request) {
        ColisDTO created = colisService.createColis(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PostMapping("/{id}/assign")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Assign colis", description = "Assign a colis to a transporteur")
    public ResponseEntity<ColisDTO> assignColis(
            @PathVariable String id,
            @Valid @RequestBody AssignColisRequest request) {
        ColisDTO assigned = colisService.assignColis(id, request);
        return ResponseEntity.ok(assigned);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update colis", description = "Update an existing colis")
    public ResponseEntity<ColisDTO> updateColis(
            @PathVariable String id,
            @Valid @RequestBody UpdateColisRequest request) {
        ColisDTO updated = colisService.updateColis(id, request);
        return ResponseEntity.ok(updated);
    }

    @PatchMapping("/{id}/statut")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update colis statut", description = "Update the statut of a colis")
    public ResponseEntity<ColisDTO> updateColisStatut(
            @PathVariable String id,
            @Valid @RequestBody UpdateStatutRequest request,
            @AuthenticationPrincipal User currentUser) {
        ColisDTO updated = colisService.updateColisStatut(currentUser, id, request);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete colis", description = "Delete a colis")
    public ResponseEntity<Void> deleteColis(@PathVariable String id) {
        colisService.deleteColis(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get colis by ID", description = "Get a specific colis by its ID")
    public ResponseEntity<ColisDTO> getColisById(
            @PathVariable String id,
            @AuthenticationPrincipal User currentUser) {
        ColisDTO colis = colisService.getColisById(currentUser, id);
        return ResponseEntity.ok(colis);
    }
}
