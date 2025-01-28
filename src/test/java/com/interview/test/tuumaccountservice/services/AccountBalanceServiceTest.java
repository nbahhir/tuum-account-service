package com.interview.test.tuumaccountservice.services;

import com.interview.test.tuumaccountservice.dto.Account;
import com.interview.test.tuumaccountservice.dto.Balance;
import com.interview.test.tuumaccountservice.dto.interfaces.AccountBalanceResponse;
import com.interview.test.tuumaccountservice.dto.interfaces.BalanceDTO;
import com.interview.test.tuumaccountservice.dto.interfaces.CreateAccountRequest;
import com.interview.test.tuumaccountservice.enums.BalanceCurrency;
import com.interview.test.tuumaccountservice.exceptions.EntryNotFoundException;
import com.interview.test.tuumaccountservice.services.core.AccountService;
import com.interview.test.tuumaccountservice.services.core.BalanceService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class AccountBalanceServiceTest {
    @Mock
    AccountService accountServiceMock;
    @Mock
    BalanceService balanceServiceMock;

    @InjectMocks
    AccountBalanceService accountBalanceService;

    @Test
    void testAccountCreation() {
        String testCustomerId = "f95926fb-7799-453d-b56c-d3bacda9694e";
        String testCountry = "Baldurs Gate";
        List<String> testCurrencies = List.of("EUR", "USD");
        CreateAccountRequest request = new CreateAccountRequest(testCustomerId, testCountry,
            testCurrencies);
        UUID testAccountId = UUID.fromString("ffe00658-35c8-4fea-a2c7-a8b7cdde8d03");

        // Meaning this account is new
        Mockito.when(accountServiceMock.findAccountByCustomerId(UUID.fromString(testCustomerId)))
            .thenReturn(Optional.empty());
        Mockito.when(accountServiceMock.createAccount(UUID.fromString(testCustomerId), testCountry))
            .thenReturn(testAccountId);

        BalanceDTO balanceDTO1 = new BalanceDTO("0", "EUR");
        BalanceDTO balanceDTO2 = new BalanceDTO("0", "USD");

        AccountBalanceResponse response = accountBalanceService.createNewAccount(request);

        Mockito.verify(balanceServiceMock, Mockito.times(2))
            .createBalance(Mockito.any(), Mockito.any());

        Assertions.assertNotNull(response);
        Assertions.assertEquals(2, response.balances().size());
        Assertions.assertEquals(testAccountId.toString(), response.accountId());
        Assertions.assertEquals(testCustomerId, response.customerId());
        Assertions.assertEquals(List.of(balanceDTO1, balanceDTO2), response.balances());
    }

    @Test
    void testAccountExists() {
        String testCustomerId = "f95926fb-7799-453d-b56c-d3bacda9694e";
        String testCountry = "Baldurs Gate";
        List<String> testCurrencies = List.of("EUR", "USD");
        CreateAccountRequest request = new CreateAccountRequest(testCustomerId, testCountry,
                testCurrencies);

        // Meaning account already exists
        Mockito.when(accountServiceMock.findAccountByCustomerId(UUID.fromString(testCustomerId)))
                .thenReturn(Optional.of(new Account(null, null, null)));
        Assertions.assertThrows(IllegalArgumentException.class,
            () -> accountBalanceService.createNewAccount(request));
    }

    @Test
    void testCreatingWithSameCurrency() {
        String testCustomerId = "f95926fb-7799-453d-b56c-d3bacda9694e";
        String testCountry = "Baldurs Gate";
        List<String> testCurrencies = List.of("EUR", "EUR");
        CreateAccountRequest request = new CreateAccountRequest(testCustomerId, testCountry,
                testCurrencies);
        UUID testAccountId = UUID.fromString("ffe00658-35c8-4fea-a2c7-a8b7cdde8d03");

        // Meaning this account is new
        Mockito.when(accountServiceMock.findAccountByCustomerId(UUID.fromString(testCustomerId)))
                .thenReturn(Optional.empty());
        Mockito.when(accountServiceMock.createAccount(UUID.fromString(testCustomerId), testCountry))
                .thenReturn(testAccountId);

        BalanceDTO balanceDTO1 = new BalanceDTO("0", "EUR");

        AccountBalanceResponse response = accountBalanceService.createNewAccount(request);

        Mockito.verify(balanceServiceMock, Mockito.times(1))
                .createBalance(Mockito.any(), Mockito.any());

        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, response.balances().size());
        Assertions.assertEquals(testAccountId.toString(), response.accountId());
        Assertions.assertEquals(testCustomerId, response.customerId());
        Assertions.assertEquals(List.of(balanceDTO1), response.balances());
    }

    @Test
    void testCreatingWrongCurrency() {
        String testCustomerId = "f95926fb-7799-453d-b56c-d3bacda9694e";
        String testCountry = "Baldurs Gate";
        List<String> testCurrencies = List.of("MEOW", "EUR");
        CreateAccountRequest request = new CreateAccountRequest(testCustomerId, testCountry,
                testCurrencies);
        UUID testAccountId = UUID.fromString("ffe00658-35c8-4fea-a2c7-a8b7cdde8d03");

        Mockito.when(accountServiceMock.findAccountByCustomerId(UUID.fromString(testCustomerId)))
                .thenReturn(Optional.empty());
        Mockito.when(accountServiceMock.createAccount(UUID.fromString(testCustomerId), testCountry))
                .thenReturn(testAccountId);

        Assertions.assertThrows(IllegalArgumentException.class,
            () -> accountBalanceService.createNewAccount(request));
    }

    @Test
    void testGetNormalAccountAndBalances()
    {
        String testRawAccountId = "f95926fb-7799-453d-b56c-d3bacda9694e";
        String testCountry = "Borderland";
        UUID testAccountId = UUID.fromString(testRawAccountId);
        UUID testCustomerId = UUID.fromString("384bad59-b007-4f30-bcbe-bad6694d1fcb");

        Balance testBalance1 = new Balance(UUID.randomUUID(), testAccountId,
                BalanceCurrency.USD, BigDecimal.ONE);
        Balance testBalance2 = new Balance(UUID.randomUUID(), testAccountId,
                BalanceCurrency.EUR, BigDecimal.ONE);
        BalanceDTO testBalanceDto1 = new BalanceDTO(testBalance1.amount().toString(),
            testBalance1.currency().toString());
        BalanceDTO testBalanceDto2 = new BalanceDTO(testBalance2.amount().toString(),
                testBalance2.currency().toString());

        List<Balance> testBalances = List.of(testBalance1, testBalance2);
        List<BalanceDTO> testBalanceDtos = List.of(testBalanceDto1, testBalanceDto2);

        Account testAccount = new Account(testCustomerId, testAccountId, testCountry);

        Mockito.when(accountServiceMock.findAccountByAccountId(testAccountId))
            .thenReturn(Optional.of(testAccount));
        Mockito.when(balanceServiceMock.findBalances(testAccountId)).thenReturn(testBalances);

        AccountBalanceResponse response = accountBalanceService.getAccountAndBalances(testRawAccountId);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(2, response.balances().size());
        Assertions.assertEquals(testBalanceDtos, response.balances());
        Assertions.assertEquals(testRawAccountId, response.accountId());
        Assertions.assertEquals(testCustomerId.toString(), response.customerId());
    }

    @Test
    void testGetAccountUserDoesntExist()
    {
        String testRawAccountId = "f95926fb-7799-453d-b56c-d3bacda9694e";
        UUID testAccountId = UUID.fromString(testRawAccountId);

        Mockito.when(accountServiceMock.findAccountByAccountId(testAccountId))
            .thenReturn(Optional.empty());

        Assertions.assertThrows(EntryNotFoundException.class,
            () -> accountBalanceService.getAccountAndBalances(testRawAccountId));
    }

    @Test
    void testGetAccountNoBalances()
    {
        String testRawAccountId = "f95926fb-7799-453d-b56c-d3bacda9694e";
        String testCountry = "Martinez";
        UUID testAccountId = UUID.fromString(testRawAccountId);
        UUID testCustomerId = UUID.fromString("384bad59-b007-4f30-bcbe-bad6694d1fcb");

        Account testAccount = new Account(testCustomerId, testAccountId, testCountry);

        Mockito.when(accountServiceMock.findAccountByAccountId(testAccountId))
                .thenReturn(Optional.of(testAccount));
        Mockito.when(balanceServiceMock.findBalances(testAccountId)).thenReturn(List.of());

        AccountBalanceResponse response = accountBalanceService.getAccountAndBalances(testRawAccountId);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(0, response.balances().size());
        Assertions.assertEquals(testRawAccountId, response.accountId());
        Assertions.assertEquals(testCustomerId.toString(), response.customerId());
    }


}
