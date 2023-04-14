package lithium.service.xp.services;

import lithium.service.Response;
import lithium.service.accounting.client.AccountingClient;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.xp.data.entities.Level;
import lithium.service.xp.data.entities.Scheme;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class XPServiceTest {

    @Mock
    private SchemeService schemeService;

    @Mock
    private LithiumServiceClientFactory services;

    @Mock
    private AccountingClient accountingClient;

    @InjectMocks
    private XPService xpService;

    @Mock
    private Response response;

    @Mock
    private Scheme scheme;

    @Test
    public void shouldReturnLevelIfExist() throws Exception {
        String playerGuid=null;
        String domainName=null;
        when(response.getData()).thenReturn(500l);
        List<Level> levels= new ArrayList<>();
        Level level=new Level();
        level.setNumber(2);
        level.setRequiredXp(500l);
        levels.add(level);
        when(scheme.getLevels()).thenReturn(levels);
        when(schemeService.findActiveScheme(domainName)).thenReturn(scheme);
        when(accountingClient.get("XP", domainName, playerGuid)).thenReturn(response);
        when(services.target(AccountingClient.class, "service-accounting-provider-internal", true))
                .thenReturn(accountingClient);
        Level xpLevel=xpService.getLevelByPlayerGuid(playerGuid,domainName);
        assertNotNull(xpLevel);
    }

}
