package com.example.productmanagementapplication.service;

import com.example.productmanagementapplication.exception.ResourceNotFoundException;
import com.example.productmanagementapplication.mapper.RetailerMapper;
import com.example.productmanagementapplication.model.dto.RetailerDto;
import com.example.productmanagementapplication.model.entity.Product;
import com.example.productmanagementapplication.model.entity.Retailer;
import com.example.productmanagementapplication.repository.ProductRepository;
import com.example.productmanagementapplication.repository.RetailerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RetailerService {

    private final RetailerRepository retailerRepository;
    private final ProductRepository productRepository;
    private final RetailerMapper retailerMapper;

    public RetailerDto createRetailer(RetailerDto dto) {
        Retailer retailer = retailerMapper.toEntity(dto);
        Retailer saved = retailerRepository.save(retailer);
        return retailerMapper.toDto(saved);
    }

    @Transactional
    public RetailerDto updateRetailer(Long id, RetailerDto dto) {
        Retailer existing = retailerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Retailer not found with id: " + id));

        existing.setName(dto.getName());
        existing.setEmail(dto.getEmail());

        // Optionally update products (could be enhanced)
        if (dto.getProducts() != null) {
            // Remove products not in dto list
            existing.getProducts().removeIf(product ->
                    dto.getProducts().stream().noneMatch(p -> p.getId() != null && p.getId().equals(product.getId()))
            );

            // Add or update products from DTO
            for (var productDto : dto.getProducts()) {
                if (productDto.getId() == null) {
                    Product newProduct = retailerMapper.toProductEntity(productDto);
                    newProduct.setRetailer(existing);
                    existing.getProducts().add(newProduct);
                } else {
                    // Update existing product (optional: implement more logic)
                }
            }
        }

        Retailer updated = retailerRepository.save(existing);
        return retailerMapper.toDto(updated);
    }

    public void deleteRetailer(Long id) {
        Retailer retailer = retailerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Retailer not found with id: " + id));
        retailerRepository.delete(retailer);
    }

    public List<RetailerDto> getAllRetailers() {
        return retailerRepository.findAll().stream()
                .map(retailerMapper::toDto)
                .collect(Collectors.toList());
    }

    public RetailerDto getRetailerById(Long id) {
        Retailer retailer = retailerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Retailer not found with id: " + id));
        return retailerMapper.toDto(retailer);
    }

    @Transactional
    public RetailerDto addProductsToRetailer(Long retailerId, List<Product> products) {
        Retailer retailer = retailerRepository.findById(retailerId)
                .orElseThrow(() -> new ResourceNotFoundException("Retailer not found with id: " + retailerId));

        for (Product product : products) {
            product.setRetailer(retailer);
        }

        retailer.getProducts().addAll(products);
        productRepository.saveAll(products);

        Retailer updated = retailerRepository.save(retailer);
        return retailerMapper.toDto(updated);
    }
}
