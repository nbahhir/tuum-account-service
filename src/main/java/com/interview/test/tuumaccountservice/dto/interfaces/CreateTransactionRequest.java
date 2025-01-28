package com.interview.test.tuumaccountservice.dto.interfaces;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Value;

@Value
public class CreateTransactionRequest {
    @NotBlank(message = "Account ID should not be null.")
    String accountId;

    @NotBlank(message = "Amount should not be null.")
    // matches positive number with 2 or less decimal points.
    @Pattern(regexp = "^\\d+(?:\\.\\d{0,2})?$", message = "Invalid amount of money.")
    String amount;

    @NotBlank(message = "Currency should not be null.")
    @Pattern(regexp = "^[A-Z]{3}$", message = "Currency should be valid.")
    String currency;

    // No specified requirements about the direction, but it made sense to me to at least limit to 10 letters.
    @NotBlank(message = "Direction should not be null.")
    @Pattern(regexp = "^[A-Za-z]{1,10}$", message = "Direction should only contain fewer than 10 letters.")
    String direction;

    // careful with description field, not allow any injection attacks.
    @NotBlank(message = "Description should not be null.")
    @Pattern(regexp = "^[A-Za-z0-9\\s.,!?'\"()-]*$", message = "Description should only contain spaces, letter, number" +
            "and common punctuation.")
    String description;
}
