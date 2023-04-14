package lithium.service.casino.provider.roxor.services.mocks;

import lithium.service.casino.exceptions.Status512ProviderNotConfiguredException;
import lithium.service.casino.provider.roxor.api.exceptions.Status400BadRequestException;
import lithium.service.casino.provider.roxor.api.exceptions.Status401NotLoggedInException;
import lithium.service.casino.provider.roxor.api.exceptions.Status406DisabledGameException;
import lithium.service.casino.provider.roxor.api.exceptions.Status500RuntimeException;
import lithium.service.casino.provider.roxor.context.GamePlayContext;
import lithium.service.casino.provider.roxor.util.ValidationHelper;
import lithium.service.games.client.objects.Game;
import lithium.service.user.client.objects.Domain;
import lithium.service.user.client.objects.LoginEvent;
import lithium.service.user.client.objects.User;
import org.mockito.Mockito;

public class BalanceServiceTestMock {

    public static ValidationHelper mockValidationHelper(boolean isNoLastLoginEvent) throws Status500RuntimeException, Status401NotLoggedInException, Status512ProviderNotConfiguredException, Status400BadRequestException, Status406DisabledGameException {
        lithium.service.user.client.objects.User user = User.builder().guid("123").build();
        lithium.service.domain.client.objects.Domain domain = lithium.service.domain.client.objects.Domain.builder().name("livescore_uganda").build();
        LoginEvent lastLoginEvent = LoginEvent.builder().user(user)
                .domain(Domain.builder().name("Heins_DomainCopy").build())
                .build();
        ValidationHelper validationHelper = Mockito.mock(ValidationHelper.class);
        Mockito.when(validationHelper.getUserGuidFromApiToken(Mockito.anyString())).thenReturn("123");
        if(isNoLastLoginEvent) {
            Mockito.when(validationHelper.findLastLoginEventForSessionKey(Mockito.any(), Mockito.anyString())).thenReturn(null);
        }else{
            Mockito.when(validationHelper.findLastLoginEventForSessionKey(Mockito.any(), Mockito.anyString())).thenReturn(lastLoginEvent);
        }
        Mockito.when(validationHelper.getDomain(Mockito.any(), Mockito.anyString())).thenReturn(domain);
        Mockito.doNothing().when(validationHelper).validate(
                Mockito.any(GamePlayContext.class),
                Mockito.any(LoginEvent.class),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyString()
        );
        Mockito.when(validationHelper.getGame(
                Mockito.any(GamePlayContext.class),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyString()
        )).thenReturn(new Game());
        return validationHelper;
    }

}
