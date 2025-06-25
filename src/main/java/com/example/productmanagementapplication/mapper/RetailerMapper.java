package com.example.productmanagementapplication.mapper;

import com.example.productmanagementapplication.model.dto.ProductDto;
import com.example.productmanagementapplication.model.dto.RetailerDto;
import com.example.productmanagementapplication.model.entity.Product;
import com.example.productmanagementapplication.model.entity.Retailer;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class RetailerMapper {

    public RetailerDto toDto(Retailer retailer) {
        if (retailer == null) return null;

        RetailerDto dto = new RetailerDto();
        dto.setId(retailer.getId());
        dto.setName(retailer.getName());
        dto.setEmail(retailer.getEmail());

        if (retailer.getProducts() != null) {
            List<ProductDto> productDtos = new ArrayList<>();
            for (Product product : retailer.getProducts()) {
                productDtos.add(toProductDto(product));
            }
            dto.setProducts(productDtos);
        }

        return dto;
    }

    public Retailer toEntity(RetailerDto dto) {
        if (dto == null) return null;

        Retailer retailer = new Retailer();
        retailer.setId(dto.getId());
        retailer.setName(dto.getName());
        retailer.setEmail(dto.getEmail());

        if (dto.getProducts() != null) {
            List<Product> products = new ArrayList<>();
            for (ProductDto productDto : dto.getProducts()) {
                Product product = toProductEntity(productDto);
                product.setRetailer(retailer);
                products.add(product);
            }
            retailer.setProducts(products);
        }

        return retailer;
    }

    public ProductDto toProductDto(Product product) {
        if (product == null) return null;

        ProductDto dto = new ProductDto();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setPrice(product.getPrice());
        dto.setRetailerId(product.getRetailer() != null ? product.getRetailer().getId() : null);

        return dto;
    }

    public Product toProductEntity(ProductDto dto) {
        if (dto == null) return null;

        Product product = new Product();
        product.setId(dto.getId());
        product.setName(dto.getName());
        product.setPrice(dto.getPrice());
        return product;
    }
}
