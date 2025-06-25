package com.example.productmanagementapplication.model.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto {

    private Long id;

    @NotBlank(message = "Name cannot be blank")
    private String name;

    @Positive(message = "Price must be a positive value")
    private double price;

    private Long retailerId;
}
