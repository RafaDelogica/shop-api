package com.project.shop_api.infrastructure.rest.dto.request;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ProductRequest {

	@NotBlank(message = "Sku must be completed")
    private String sku;
	
//	@NotBlank(message = "Sku must be completed")
//    private String name;
	
	@Size(max = 2000, message = "The description must not exceed 2,000 characters")
    private String description;
	
	@NotNull(message = "El precio es obligatorio")
	@DecimalMin(value = "0.01", message = "The price must be bigger than 0")
    private BigDecimal price;
	

	@NotNull(message = "Stock must be completed")
    @Min(value = 0, message = "Stock must not be negative")
    private Integer stock;
	
    private boolean active;
}
