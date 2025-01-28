package com.interview.test.tuumaccountservice.dto;

import java.util.UUID;

public record Account(UUID customerId, UUID accountId, String country) {}
