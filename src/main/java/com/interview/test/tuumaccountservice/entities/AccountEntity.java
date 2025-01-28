package com.interview.test.tuumaccountservice.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountEntity {
    private UUID accountId;
    private UUID customerId;
    private String country;

}
