package lithium.service.casino.provider.roxor.services.mocks;

import lithium.service.casino.CasinoClientService;
import lithium.service.casino.client.objects.response.BalanceResponse;
import lithium.service.casino.exceptions.Status500UnhandledCasinoClientException;
import org.mockito.Mockito;

public class CasinoClientServiceTestMock {
    public static CasinoClientService mockCasinoClient() throws Status500UnhandledCasinoClientException {
        BalanceResponse balanceResponse = BalanceResponse.builder().balanceCents(10L).build();
        CasinoClientService casinoService = Mockito.mock(CasinoClientService.class);
        Mockito.when( casinoService.getPlayerBalance(
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyString())).thenReturn(balanceResponse);

        return casinoService;
    }

}
