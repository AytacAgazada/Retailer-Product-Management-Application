package com.example.productmanagementapplication.controller;

import com.example.productmanagementapplication.model.dto.RetailerDto;
import com.example.productmanagementapplication.service.RetailerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/retailers")
@RequiredArgsConstructor
public class RetailerController {

    private final RetailerService retailerService;

    @PostMapping
    public ResponseEntity<RetailerDto> createRetailer(@Valid @RequestBody RetailerDto dto) {
        return ResponseEntity.ok(retailerService.createRetailer(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RetailerDto> updateRetailer(@PathVariable Long id, @Valid @RequestBody RetailerDto dto) {
        return ResponseEntity.ok(retailerService.updateRetailer(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRetailer(@PathVariable Long id) {
        retailerService.deleteRetailer(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<RetailerDto>> getAllRetailers() {
        return ResponseEntity.ok(retailerService.getAllRetailers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RetailerDto> getRetailerById(@PathVariable Long id) {
        return ResponseEntity.ok(retailerService.getRetailerById(id));
    }
}
