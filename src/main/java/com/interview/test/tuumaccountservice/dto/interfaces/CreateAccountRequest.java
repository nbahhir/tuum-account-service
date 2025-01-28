package com.interview.test.tuumaccountservice.dto.interfaces;

import jakarta.validation.constraints.NotBlank;
import lombok.Value;

import java.util.List;

// DTO validation to reduce amount of code and prevent injection attacks.
@Value
public class CreateAccountRequest {
    @NotBlank(message="Customer ID should not be null.")
    String customerId;

    @NotBlank(message="Country should not be null.")
    String country;

    List<String> currencies;
}
