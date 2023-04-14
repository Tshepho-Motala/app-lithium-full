package lithium.service.casino.provider.iforium.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lithium.service.casino.provider.iforium.AbstractBalance;
import lithium.service.casino.provider.iforium.constant.TestConstants;
import lithium.service.casino.provider.iforium.model.request.BalanceRequest;
import lithium.service.casino.provider.iforium.model.response.Balance;
import lithium.service.casino.provider.iforium.model.response.BalanceResponse;
import lithium.service.casino.provider.iforium.model.response.ErrorCodes;
import lithium.service.casino.provider.iforium.service.impl.BalanceServiceImpl;
import lithium.service.domain.client.exceptions.Status550ServiceDomainClientException;
import lithium.service.user.client.exceptions.Status411UserNotFoundException;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static lithium.service.casino.provider.iforium.constant.TestConstants.DOMAIN_NAME;
import static lithium.service.casino.provider.iforium.constant.TestConstants.GBP_CURRENCY;
import static lithium.service.casino.provider.iforium.util.Fixtures.fixture;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;

@ExtendWith({MockitoExtension.class})
class BalanceServiceUTest extends AbstractBalance {

    BalanceService balanceService;

    ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockExternalServices();

        objectMapper = new ObjectMapper();
        balanceService = new BalanceServiceImpl(cachingDomainClientService, casinoClientService, lithiumClientUtils);
    }

    @Test
    @SneakyThrows
    void testShouldReturnSuccessBalanceResponse() {
        mockSuccessGetLastLoginEvent();
        Mockito.when(cachingDomainClientService.retrieveDomainFromDomainService(anyString())).thenReturn(validDomain());
        Mockito.when(casinoClientService.getPlayerBalance(anyString(), anyString(), anyString()))
               .thenReturn(validCasinoClientBalanceResponse("10000"));

        BalanceResponse expected = validBalanceResponse("100.0");
        BalanceResponse actual = balanceService.balance(validBalanceRequest(), DOMAIN_NAME);

        assertEquals(expected, actual);
    }

    @Test
    @SneakyThrows
    void testShouldReturnStatus411UserNotFoundException_WhenPlayerIdDoesNotExist() {
        mockFailureGetLastLoginEvent();
        Mockito.when(cachingDomainClientService.retrieveDomainFromDomainService(anyString())).thenReturn(validDomain());

        Status411UserNotFoundException exception = assertThrows(Status411UserNotFoundException.class, () -> {
            balanceService.balance(validBalanceRequest(), DOMAIN_NAME);
        });

        assertThat(exception.getMessage()).contains("user is invalid");
    }

    @Test
    @SneakyThrows
    void testShouldReturnDomainNotFoundException_WhenDomainNotExist() {
        Mockito.when(cachingDomainClientService.retrieveDomainFromDomainService(anyString()))
               .thenThrow(new Status550ServiceDomainClientException("Unable to retrieve domain from domain service: domain"));

        Status550ServiceDomainClientException exception = assertThrows(Status550ServiceDomainClientException.class, () ->
                balanceService.balance(validBalanceRequest(), DOMAIN_NAME));

        assertThat(exception.getMessage()).contains("Unable to retrieve domain from domain service: domain");
    }

    @Test
    @SneakyThrows
    void testShouldReturnSuccessBalanceResponseWithZeroBalance_WhenUserNotExist() {
        mockSuccessGetLastLoginEvent();
        Mockito.when(cachingDomainClientService.retrieveDomainFromDomainService(anyString())).thenReturn(validDomain());
        Mockito.when(casinoClientService.getPlayerBalance(anyString(), anyString(), anyString()))
               .thenReturn(validCasinoClientBalanceResponse("0"));

        BalanceResponse expected = validBalanceResponse("0.0");
        BalanceResponse actual = balanceService.balance(validBalanceRequest(), DOMAIN_NAME);

        assertEquals(expected, actual);
    }

    @SneakyThrows
    private BalanceRequest validBalanceRequest() {
        return objectMapper.readValue(
                fixture(TestConstants.BALANCE_REQUEST_FIXTURE_PATH, TestConstants.PLATFORM_KEY, TestConstants.SEQUENCE,
                        TestConstants.TIMESTAMP, TestConstants.OPERATOR_ACCOUNT_ID), BalanceRequest.class);
    }

    @SneakyThrows
    private BalanceResponse validBalanceResponse(String cashFunds) {
        return BalanceResponse.builder()
                              .errorCode(ErrorCodes.SUCCESS.getCode())
                              .balance(Balance.builder()
                                              .currencyCode(GBP_CURRENCY)
                                              .bonusFunds(new BigDecimal("0.0"))
                                              .cashFunds(new BigDecimal(cashFunds))
                                              .fundsPriority("Unknown")
                                              .build()
                              ).build();
    }
}