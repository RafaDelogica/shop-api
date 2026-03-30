package com.project.shop_api.infrastructure.mapper.entity;

import com.project.shop_api.domain.model.Customer;
import com.project.shop_api.domain.model.Address;
import com.project.shop_api.infrastructure.persistence.entity.CustomerEntity;
import com.project.shop_api.infrastructure.persistence.entity.AddressEntity;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CustomerEntityMapperTest {

    private final CustomerEntityMapper mapper =
            Mappers.getMapper(CustomerEntityMapper.class);

    @Test
    void toEntity_shouldMapCorrectly() {

        Customer domain = Customer.builder()
                .id(1L)
                .fullName("Rafa")
                .phone("1234")
                .email("r@test.com")
                .build();

        CustomerEntity entity = mapper.toEntity(domain);

        assertEquals(1L, entity.getId());
        assertEquals("Rafa", entity.getFullName());
        assertEquals("r@test.com", entity.getEmail());
        assertEquals("1234", entity.getPhone());
    }



    @Test
    void toModel_shouldMapCorrectly() {

        CustomerEntity entity = CustomerEntity.builder()
                .id(1L)
                .fullName("Rafa")
                .email("r@test.com")
                .phone("1234")
                .addresses(List.of())
                .build();

        Customer model = mapper.toModel(entity);

        assertEquals(1L, model.getId());
        assertEquals("Rafa", model.getFullName());
        assertEquals("r@test.com", model.getEmail());
    }

    @Test
    void nullInput_shouldReturnNull() {
        assertNull(mapper.toModel(null));
        assertNull(mapper.toEntity(null));
    }
}