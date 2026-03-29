package com.project.shop_api.application.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.project.shop_api.application.common.exception.ConflictException;
import com.project.shop_api.application.common.exception.ForbiddenException;
import com.project.shop_api.application.common.exception.ResourceNotFoundException;
import com.project.shop_api.application.service.impl.CustomerServiceImpl;
import com.project.shop_api.domain.model.Address;
import com.project.shop_api.domain.model.Customer;
import com.project.shop_api.domain.repository.AddressRepository;
import com.project.shop_api.domain.repository.CustomerRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

@ExtendWith(MockitoExtension.class)
class CustomerServiceImplTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private AddressRepository addressRepository;

    @InjectMocks
    private CustomerServiceImpl service;

    private Customer baseCustomer;

    @BeforeEach
    void setup() {
        baseCustomer = Customer.builder()
                .id(1L)
                .fullName("Rafael Test")
                .email("rafa@test.com")
                .phone("123456")
                .addresses(new ArrayList<>())
                .build();
    }

    // ============================================================
    // CREATE
    // ============================================================

    @Test
    void create_shouldThrowConflict_whenEmailExists() {

        Customer newCustomer = Customer.builder()
                .email("existing@test.com")
                .build();

        when(customerRepository.existsEmail("existing@test.com")).thenReturn(true);

        assertThrows(ConflictException.class,
                () -> service.create(newCustomer));

        verify(customerRepository, never()).save(any());
    }

    @Test
    void create_shouldSave_whenEmailIsUnique() {

        Customer newCustomer = Customer.builder()
                .fullName("Nuevo")
                .email("new@test.com")
                .build();

        when(customerRepository.existsEmail("new@test.com")).thenReturn(false);
        when(customerRepository.save(newCustomer)).thenReturn(
                Customer.builder()
                        .id(99L)
                        .email("new@test.com")
                        .build()
        );

        Customer result = service.create(newCustomer);

        assertEquals(99L, result.getId());
        verify(customerRepository).save(newCustomer);
    }

    // ============================================================
    // UPDATE
    // ============================================================

    @Test
    void update_shouldThrowNotFound_whenCustomerMissing() {

        Customer updateData = Customer.builder().email("a@test.com").build();

        when(customerRepository.findById(5L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> service.update(5L, updateData));
    }

    @Test
    void update_shouldThrowConflict_whenEmailAlreadyExists() {

        Customer updateData = Customer.builder()
                .fullName("Test2")
                .email("duplicate@test.com")
                .build();

        when(customerRepository.findById(1L)).thenReturn(Optional.of(baseCustomer));
        when(customerRepository.existsEmail("duplicate@test.com")).thenReturn(true);

        assertThrows(ConflictException.class,
                () -> service.update(1L, updateData));
    }

    @Test
    void update_shouldUpdate_whenValid() {

        Customer updateData = Customer.builder()
                .fullName("New Name")
                .email("rafa@test.com") // same email → no conflict
                .build();

        when(customerRepository.findById(1L)).thenReturn(Optional.of(baseCustomer));
        when(customerRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Customer result = service.update(1L, updateData);

        assertEquals("New Name", result.getFullName());
    }

    // ============================================================
    // FIND BY ID
    // ============================================================

    @Test
    void findById_shouldThrowNotFound_whenMissing() {

        when(customerRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> service.findById(2L));
    }

    @Test
    void findById_shouldReturnCustomer_whenExists() {

        when(customerRepository.findById(1L)).thenReturn(Optional.of(baseCustomer));

        Customer result = service.findById(1L);

        assertEquals(1L, result.getId());
    }

    // ============================================================
    // FIND ALL
    // ============================================================

    @Test
    void findAll_shouldReturnPage() {

        Page<Customer> page = new PageImpl<>(List.of(baseCustomer));

        when(customerRepository.findAll(null, 0, 10))
                .thenReturn(page);

        Page<Customer> result = service.findAll(null, 0, 10);

        assertEquals(1, result.getTotalElements());
    }

    // ============================================================
    // DELETE
    // ============================================================

    @Test
    void delete_shouldThrowNotFound_whenMissing() {

        when(customerRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> service.delete(1L));
    }

    @Test
    void delete_shouldWork_whenExists() {

        when(customerRepository.findById(1L)).thenReturn(Optional.of(baseCustomer));

        service.delete(1L);

        verify(customerRepository).deleteById(1L);
    }

    // ============================================================
    // ADD ADDRESS
    // ============================================================

    @Test
    void addAddress_shouldSetFirstAddressAsDefault() {

        Address addr = Address.builder()
                .line1("Calle 1")
                .city("Madrid")
                .postalCode("28001")
                .country("Spain")
                .build();

        when(customerRepository.findById(1L)).thenReturn(Optional.of(baseCustomer));
        when(addressRepository.countByCustomerId(1L)).thenReturn(0L);
        when(addressRepository.save(any())).thenAnswer(inv -> {
            Address a = inv.getArgument(0);
            a.setId(10L);
            return a;
        });
        when(customerRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Customer result = service.addAddress(1L, addr);

        assertEquals(1, result.getAddresses().size());
        assertTrue(result.getAddresses().get(0).getIsDefault());
    }

    @Test
    void addAddress_shouldNotSetDefault_whenNotFirst() {

        Address addr = Address.builder().line1("Otra calle").city("Madrid").postalCode("28002").country("Spain").build();

        when(customerRepository.findById(1L)).thenReturn(Optional.of(baseCustomer));
        when(addressRepository.countByCustomerId(1L)).thenReturn(2L);
        when(addressRepository.save(any())).thenAnswer(inv -> {
            Address a = inv.getArgument(0);
            a.setId(11L);
            return a;
        });
        when(customerRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Customer result = service.addAddress(1L, addr);

        assertFalse(result.getAddresses().get(0).getIsDefault());
    }

    // ============================================================
    // MARK DEFAULT
    // ============================================================

    @Test
    void markDefault_shouldThrowForbidden_whenAddressNotBelonging() {

        when(addressRepository.existsByIdAndCustomerId(5L, 1L)).thenReturn(false);

        when(customerRepository.findById(1L)).thenReturn(Optional.of(baseCustomer));

        assertThrows(ForbiddenException.class,
                () -> service.markDefault(1L, 5L));
    }

    @Test
    void markDefault_shouldSetDefaultCorrectly() {

        Address addr1 = Address.builder().id(10L).isDefault(true).build();
        Address addr2 = Address.builder().id(20L).isDefault(false).build();

        Customer c = Customer.builder()
                .id(1L)
                .email("test@test.com")
                .addresses(List.of(addr1, addr2))
                .build();

        when(addressRepository.existsByIdAndCustomerId(20L, 1L)).thenReturn(true);
        when(customerRepository.findById(1L)).thenReturn(Optional.of(c));
        when(customerRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Customer result = service.markDefault(1L, 20L);

        assertTrue(result.getAddresses().get(1).getIsDefault());
        assertFalse(result.getAddresses().get(0).getIsDefault());
    }
}