package com.project.shop_api.infrastructure.mapper.dto;

import com.project.shop_api.domain.model.Address;
import com.project.shop_api.infrastructure.rest.dto.request.AddressRequest;
import com.project.shop_api.infrastructure.rest.dto.response.AddressResponse;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.*;

class AddressDtoMapperTest {

    private final AddressDtoMapper mapper = Mappers.getMapper(AddressDtoMapper.class);

    @Test
    void toDomain_shouldMapCorrectly() {

        AddressRequest req = new AddressRequest();
        req.setLine1("Calle 1");
        req.setLine2("Piso 2");
        req.setCity("Madrid");
        req.setPostalCode("28001");
        req.setCountry("España");

        Address dom = mapper.toDomain(req);

        assertEquals("Calle 1", dom.getLine1());
        assertEquals("Piso 2", dom.getLine2());
        assertEquals("Madrid", dom.getCity());
        assertEquals("28001", dom.getPostalCode());
        assertEquals("España", dom.getCountry());
    }

    @Test
    void toResponse_shouldMapCorrectly() {

        Address dom = Address.builder()
                .id(22L)
                .line1("Gran Via")
                .city("Madrid")
                .postalCode("28001")
                .country("Spain")
                .isDefault(true)
                .build();

        AddressResponse res = mapper.toResponse(dom);

        assertEquals(22L, res.getId());
        assertEquals("Gran Via", res.getLine1());
        assertEquals("Madrid", res.getCity());
        assertEquals("28001", res.getPostalCode());
        assertEquals("Spain", res.getCountry());
        assertTrue(res.getIsDefault());
    }

    @Test
    void nullInput_shouldReturnNull() {
        assertNull(mapper.toDomain(null));
        assertNull(mapper.toResponse(null));
    }
}