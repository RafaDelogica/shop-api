package com.project.shop_api.infrastructure.rest.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AddressResponse {

    private Long id;

    private String line1;
    
    private String line2;

    private String city;
    
    private String postalCode;
    
    private String country;

    private Boolean isDefault;
}