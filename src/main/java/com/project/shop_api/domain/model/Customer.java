package com.project.shop_api.domain.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Customer {

    private Long id;

    private String fullName;

    private String email;

    private String phone;

    @Builder.Default
    private List<Address> addresses = new ArrayList<>();

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}

