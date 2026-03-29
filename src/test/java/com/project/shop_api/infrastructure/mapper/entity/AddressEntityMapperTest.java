package com.project.shop_api.infrastructure.mapper.entity;

import com.project.shop_api.domain.model.Address;
import com.project.shop_api.infrastructure.persistence.entity.AddressEntity;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.*;

class AddressEntityMapperTest {

    private final AddressEntityMapper mapper =
            Mappers.getMapper(AddressEntityMapper.class);

    @Test
    void toEntity_shouldMapCorrectly_andIgnoreCustomer() {

        Address domain = Address.builder()
                .id(44L)
                .line1("Calle Falsa 123")
                .city("Madrid")
                .postalCode("28001")
                .country("Spain")
                .isDefault(true)
                .build();

        AddressEntity entity = mapper.toEntity(domain);

        assertEquals(44L, entity.getId());
        assertEquals("Calle Falsa 123", entity.getLine1());
        assertEquals("Madrid", entity.getCity());
        assertTrue(entity.isDefault());

        // MUY IMPORTANTE
        assertNull(entity.getCustomer(), "customer MUST be ignored in mapper");
    }

    @Test
    void toModel_shouldMapCorrectly() {

        AddressEntity entity = AddressEntity.builder()
                .id(44L)
                .line1("Calle Falsa 123")
                .city("Madrid")
                .postalCode("28001")
                .country("Spain")
                .isDefault(true)
                .build();

        Address model = mapper.toModel(entity);

        assertEquals(44L, model.getId());
        assertEquals("Calle Falsa 123", model.getLine1());
        assertEquals("Madrid", model.getCity());
    }

    @Test
    void nullInput_shouldReturnNull() {
        assertNull(mapper.toEntity(null));
        assertNull(mapper.toModel(null));
    }
}