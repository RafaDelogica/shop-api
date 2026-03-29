package com.project.shop_api.infrastructure.mapper.dto;

import com.project.shop_api.domain.model.Customer;
import com.project.shop_api.infrastructure.rest.dto.request.CustomerRequest;
import com.project.shop_api.infrastructure.rest.dto.response.CustomerResponse;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.*;

class CustomerDtoMapperTest {

    private final CustomerDtoMapper mapper = Mappers.getMapper(CustomerDtoMapper.class);

    @Test
    void toDomain_shouldMapCorrectly() {

        CustomerRequest req = new CustomerRequest();
        req.setFullName("Rafael Manuel");
        req.setEmail("rafa@test.com");
        req.setPhone("12345");

        Customer domain = mapper.toDomain(req);

        assertEquals("Rafael Manuel", domain.getFullName());
        assertEquals("rafa@test.com", domain.getEmail());
        assertEquals("12345", domain.getPhone());
    }

    @Test
    void toResponse_shouldMapCorrectly() {

        Customer domain = Customer.builder()
                .id(10L)
                .fullName("Rafa")
                .email("r@test.com")
                .phone("6789")
                .build();

        CustomerResponse res = mapper.toResponse(domain);

        assertEquals(10L, res.getId());
        assertEquals("Rafa", res.getFullName());
        assertEquals("r@test.com", res.getEmail());
        assertEquals("6789", res.getPhone());
    }

    @Test
    void nullInput_shouldReturnNull() {
        assertNull(mapper.toDomain(null));
        assertNull(mapper.toResponse(null));
    }
}