package com.project.shop_api.infrastructure.rest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.shop_api.application.common.exception.ConflictException;
import com.project.shop_api.application.common.exception.ForbiddenException;
import com.project.shop_api.application.common.exception.ResourceNotFoundException;
import com.project.shop_api.application.service.CustomerService;
import com.project.shop_api.domain.model.Address;
import com.project.shop_api.domain.model.Customer;
import com.project.shop_api.infrastructure.mapper.dto.AddressDtoMapper;
import com.project.shop_api.infrastructure.mapper.dto.CustomerDtoMapper;
import com.project.shop_api.infrastructure.rest.dto.request.AddressRequest;
import com.project.shop_api.infrastructure.rest.dto.request.CustomerRequest;
import com.project.shop_api.infrastructure.rest.dto.response.CustomerResponse;
import com.project.shop_api.infrastructure.rest.exception.GlobalExceptionHandler;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@WebMvcTest(CustomerController.class)
@Import(GlobalExceptionHandler.class)
class CustomerControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private CustomerService service;

	@MockBean
	private CustomerDtoMapper customerMapper;

	@MockBean
	private AddressDtoMapper addressMapper;

	// ---- FIX para activar Jakarta Validation en WebMvcTest ----
	@TestConfiguration
	static class ValidationConfig {
		@Bean
		public LocalValidatorFactoryBean validator() {
			return new LocalValidatorFactoryBean();
		}
	}

	// ============================================================
	// POST /api/customers (CREATE)
	// ============================================================
	@Test
	void create_shouldReturn200_whenValidRequest() throws Exception {

		CustomerRequest request = new CustomerRequest();
		request.setFullName("John Doe");
		request.setEmail("john@example.com");
		request.setPhone("12345");

		Customer domain = Customer.builder().id(1L).fullName("John Doe").email("john@example.com").build();

		when(customerMapper.toDomain(any())).thenReturn(domain);
		when(service.create(domain)).thenReturn(domain);
		when(customerMapper.toResponse(domain))
				.thenReturn(CustomerResponse.builder().id(1L).fullName("John Doe").email("john@example.com").build());

		mockMvc.perform(post("/api/customers").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))).andExpect(status().isOk());
	}

	@Test
	void create_shouldReturnConflict_whenEmailExists() throws Exception {

		CustomerRequest request = new CustomerRequest();
		request.setFullName("John");
		request.setEmail("john@example.com");
		request.setPhone("111");

		when(customerMapper.toDomain(any())).thenReturn(Customer.builder().build());
		when(service.create(any())).thenThrow(new ConflictException("Email already exists"));

		mockMvc.perform(post("/api/customers").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))).andExpect(status().isConflict())
				.andExpect(jsonPath("$.code").value("CONFLICT"))
				.andExpect(jsonPath("$.message").value("Email already exists"));
	}

	@Test
	void create_shouldReturnValidationError_whenInvalidRequest() throws Exception {

		CustomerRequest request = new CustomerRequest();
		request.setFullName(""); // @NotBlank
		request.setEmail("bad email"); // invalid format

		mockMvc.perform(post("/api/customers").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))).andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.code").value("VALIDATION_ERROR")).andExpect(jsonPath("$.details").isArray());
	}

	// ============================================================
	// GET /api/customers/{id}
	// ============================================================
	@Test
	void getById_shouldReturnNotFound_whenMissing() throws Exception {

		when(service.findById(10L)).thenThrow(new ResourceNotFoundException("Customer not found"));

		mockMvc.perform(get("/api/customers/10")).andExpect(status().isNotFound())
				.andExpect(jsonPath("$.code").value("RESOURCE_NOT_FOUND"))
				.andExpect(jsonPath("$.message").value("Customer not found"));
	}

	@Test
	void getById_shouldReturn200_whenExists() throws Exception {

		Customer domain = Customer.builder().id(1L).email("john@example.com").build();

		when(service.findById(1L)).thenReturn(domain);
		when(customerMapper.toResponse(domain))
				.thenReturn(CustomerResponse.builder().id(1L).email("john@example.com").build());

		mockMvc.perform(get("/api/customers/1")).andExpect(status().isOk());
	}

	// ============================================================
	// PUT /api/customers/{id}
	// ============================================================
	@Test
	void update_shouldReturnConflict_whenDuplicateEmail() throws Exception {

		CustomerRequest req = new CustomerRequest();
		req.setFullName("John");
		req.setEmail("dup@test.com");

		when(customerMapper.toDomain(any())).thenReturn(Customer.builder().build());
		when(service.update(eq(1L), any())).thenThrow(new ConflictException("Email exists"));

		mockMvc.perform(put("/api/customers/1").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(req))).andExpect(status().isConflict())
				.andExpect(jsonPath("$.code").value("CONFLICT"));
	}

	// ============================================================
	// DELETE /api/customers/{id}
	// ============================================================
	@Test
	void delete_shouldReturn204() throws Exception {

		mockMvc.perform(delete("/api/customers/1")).andExpect(status().isNoContent());

		verify(service).delete(1L);
	}

	// ============================================================
	// POST /api/customers/{id}/addresses
	// ============================================================
	@Test
	void addAddress_shouldReturn200() throws Exception {

		AddressRequest req = new AddressRequest();
		req.setLine1("Calle");
		req.setCity("Madrid");
		req.setPostalCode("28001");
		req.setCountry("Spain");

		Address domainAddress = Address.builder().id(50L).build();
		Customer domainCustomer = Customer.builder().id(1L).addresses(List.of(domainAddress)).build();

		when(addressMapper.toDomain(any())).thenReturn(domainAddress);
		when(service.addAddress(eq(1L), eq(domainAddress))).thenReturn(domainCustomer);
		when(customerMapper.toResponse(domainCustomer)).thenReturn(CustomerResponse.builder().id(1L).build());

		mockMvc.perform(post("/api/customers/1/addresses").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(req))).andExpect(status().isOk());
	}

	// ============================================================
	// PUT /api/customers/{id}/addresses/{addressId}/default
	// ============================================================
	@Test
	void defaultAddress_shouldReturnForbidden_whenNoOwnership() throws Exception {

		when(service.markDefault(eq(1L), eq(33L))).thenThrow(new ForbiddenException("Not belongs"));

		mockMvc.perform(put("/api/customers/1/addresses/33/default")).andExpect(status().isForbidden())
				.andExpect(jsonPath("$.code").value("FORBIDDEN"));
	}

	// Filter by email
	@Test
	void list_shouldFilterByEmail() throws Exception {

		Customer domain = Customer.builder().id(2L).fullName("Juan Testing").email("juan@test.com").build();

		CustomerResponse response = CustomerResponse.builder().id(2L).fullName("Juan Testing").email("juan@test.com")
				.build();

		Page<Customer> page = new PageImpl<>(List.of(domain), PageRequest.of(0, 5), 1 // totalElements
		);

		when(service.findAll("juan@test.com", 0, 5)).thenReturn(page);
		when(customerMapper.toResponse(domain)).thenReturn(response);

		mockMvc.perform(get("/api/customers").param("email", "juan@test.com").param("page", "0").param("size", "5"))
				.andExpect(status().isOk()).andExpect(jsonPath("$.content[0].email").value("juan@test.com"))
				.andExpect(jsonPath("$.totalElements").value(1)).andExpect(jsonPath("$.size").value(5))
				.andExpect(jsonPath("$.number").value(0));
	}

	@Test
	void update_shouldReturn200_whenValid() throws Exception {

		CustomerRequest req = new CustomerRequest();
		req.setFullName("John Updated");
		req.setEmail("john.updated@example.com");
		req.setPhone("777777");

		Customer domainInput = Customer.builder().fullName("John Updated").email("john.updated@example.com")
				.phone("777777").build();

		Customer domainOutput = Customer.builder().id(1L).fullName("John Updated").email("john.updated@example.com")
				.phone("777777").build();

		CustomerResponse response = CustomerResponse.builder().id(1L).fullName("John Updated")
				.email("john.updated@example.com").build();

		when(customerMapper.toDomain(req)).thenReturn(domainInput);
		when(service.update(1L, domainInput)).thenReturn(domainOutput);
		when(customerMapper.toResponse(domainOutput)).thenReturn(response);

		mockMvc.perform(put("/api/customers/1").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(req))).andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(1)).andExpect(jsonPath("$.fullName").value("John Updated"))
				.andExpect(jsonPath("$.email").value("john.updated@example.com"));

		verify(customerMapper).toDomain(req);
		verify(service).update(1L, domainInput);
		verify(customerMapper).toResponse(domainOutput);
	}

	@Test
	void setDefaultAddress_shouldReturn200_whenSuccess() throws Exception {

		Customer domainUpdated = Customer.builder().id(1L).email("john@example.com").addresses(List.of()).build();

		CustomerResponse response = CustomerResponse.builder().id(1L).email("john@example.com").build();

		when(service.markDefault(1L, 22L)).thenReturn(domainUpdated);
		when(customerMapper.toResponse(domainUpdated)).thenReturn(response);

		mockMvc.perform(put("/api/customers/1/addresses/22/default")).andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(1)).andExpect(jsonPath("$.email").value("john@example.com"));

		verify(service).markDefault(1L, 22L);
	}

}