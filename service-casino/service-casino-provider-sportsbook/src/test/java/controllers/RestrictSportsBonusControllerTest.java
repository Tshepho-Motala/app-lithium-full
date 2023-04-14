package controllers;

import lithium.service.Response;
import lithium.service.casino.client.objects.BonusRestrictionRequest;
import lithium.service.casino.provider.sportsbook.controllers.system.SystemSportsbookBonusRestrictionController;
import lithium.service.casino.provider.sportsbook.response.BonusRestrictionResponse;
import lithium.service.casino.provider.sportsbook.services.BonusRestrictionService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class RestrictSportsBonusControllerTest {
    @Mock
    private BonusRestrictionService bonusRestrictionService;


    @InjectMocks
    private SystemSportsbookBonusRestrictionController restrictSportsBonusController;

    @Test
    public void shouldReturnStatusOkWhenTheRequestIsSuccessful() throws Exception {
        BonusRestrictionRequest request =BonusRestrictionRequest.builder()
                .playerGuid("livescore_uk/rivalani06")
                .restricted(true)
                .playerId(2021)
                .build();

        Mockito.when(bonusRestrictionService.toggle(request, "livescore_uk")).thenReturn(BonusRestrictionResponse.builder().errorCode(0).build());

        Response response = restrictSportsBonusController.toggleBonusRestriction(request, "livescore_uk");

        Mockito.verify(bonusRestrictionService, Mockito.times(1)).toggle(Mockito.any(BonusRestrictionRequest.class),Mockito.anyString());

        assertEquals(Response.Status.OK, response.getStatus());
    }
}
