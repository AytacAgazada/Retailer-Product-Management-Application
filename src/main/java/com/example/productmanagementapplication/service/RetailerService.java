package com.example.productmanagementapplication.service;

import com.example.productmanagementapplication.exception.ResourceNotFoundException;
import com.example.productmanagementapplication.mapper.RetailerMapper;
import com.example.productmanagementapplication.model.dto.ProductDto;
import com.example.productmanagementapplication.model.dto.RetailerDto;
import com.example.productmanagementapplication.model.entity.Product;
import com.example.productmanagementapplication.model.entity.Retailer;
import com.example.productmanagementapplication.repository.ProductRepository;
import com.example.productmanagementapplication.repository.RetailerRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RetailerService {

    private final RetailerRepository retailerRepository;
    private final ProductRepository productRepository;
    private final RetailerMapper retailerMapper;

    private static final Logger log = LoggerFactory.getLogger(RetailerService.class);

    @CacheEvict(value = "retailers", key = "'all'")
    public RetailerDto createRetailer(RetailerDto dto) {
        Retailer retailer = retailerMapper.toEntity(dto);
        Retailer saved = retailerRepository.save(retailer);
        log.info("Created new retailer with id {}", saved.getId());
        return retailerMapper.toDto(saved);
    }

    @CachePut(value = "retailers", key = "#id")
    @Transactional
    public RetailerDto updateRetailer(Long id, RetailerDto dto) {
        Retailer existing = retailerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Retailer not found with id: " + id));

        existing.setName(dto.getName());
        existing.setEmail(dto.getEmail());

        if (dto.getProducts() != null) {
            Map<Long, Product> existingProductsMap = existing.getProducts().stream()
                    .collect(Collectors.toMap(Product::getId, p -> p));

            Set<Long> incomingProductIds = dto.getProducts().stream()
                    .filter(p -> p.getId() != null)
                    .map(ProductDto::getId)
                    .collect(Collectors.toSet());

            existing.getProducts().removeIf(product -> !incomingProductIds.contains(product.getId()));

            for (ProductDto productDto : dto.getProducts()) {
                if (productDto.getId() == null) {
                    Product newProduct = retailerMapper.toProductEntity(productDto);
                    newProduct.setRetailer(existing);
                    existing.getProducts().add(newProduct);
                } else {
                    Product productToUpdate = existingProductsMap.get(productDto.getId());
                    if (productToUpdate != null) {
                        productToUpdate.setName(productDto.getName());
                        productToUpdate.setPrice(productDto.getPrice());
                    }
                }
            }
        }

        Retailer updated = retailerRepository.save(existing);
        log.info("Updated retailer with id {}", id);
        return retailerMapper.toDto(updated);
    }

    @CacheEvict(value = "retailers", key = "'all'")
    public void deleteRetailer(Long id) {
        Retailer retailer = retailerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Retailer not found with id: " + id));
        retailerRepository.delete(retailer);
        log.info("Deleted retailer with id {}", id);
    }

    @Cacheable(value = "retailers", key = "'all'")
    public List<RetailerDto> getAllRetailers() {
        log.info("Fetching all retailers");
        return retailerRepository.findAll().stream()
                .map(retailerMapper::toDto)
                .collect(Collectors.toList());
    }

    @Cacheable(value = "retailers", key = "#id")
    public RetailerDto getRetailerById(Long id) {
        log.info("Fetching retailer by id {}", id);
        Retailer retailer = retailerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Retailer not found with id: " + id));
        return retailerMapper.toDto(retailer);
    }

    @CacheEvict(value = "retailers", key = "'all'")
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
        log.info("Added products to retailer with id {}", retailerId);
        return retailerMapper.toDto(updated);
    }

}
