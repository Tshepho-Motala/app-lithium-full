package lithium.service.casino.provider.roxor.services;

import lithium.exceptions.Status401UnAuthorisedException;
import lithium.exceptions.Status405UserDisabledException;
import lithium.service.casino.exceptions.Status512ProviderNotConfiguredException;
import lithium.service.casino.provider.roxor.api.exceptions.Status400BadRequestException;
import lithium.service.casino.provider.roxor.api.exceptions.Status401NotLoggedInException;
import lithium.service.casino.provider.roxor.api.exceptions.Status406DisabledGameException;
import lithium.service.casino.provider.roxor.api.exceptions.Status500RuntimeException;
import lithium.service.casino.provider.roxor.context.GamePlayContext;
import lithium.service.casino.provider.roxor.storage.repositories.GamePlayRepository;
import lithium.service.casino.provider.roxor.util.ValidationHelper;
import lithium.service.games.client.objects.Game;
import lithium.service.limit.client.LimitInternalSystemService;
import lithium.service.limit.client.exceptions.Status490SoftSelfExclusionException;
import lithium.service.limit.client.exceptions.Status491PermanentSelfExclusionException;
import lithium.service.limit.client.exceptions.Status496PlayerCoolingOffException;
import lithium.service.limit.client.exceptions.Status500LimitInternalSystemClientException;
import lithium.service.user.client.exceptions.Status500UserInternalSystemClientException;
import lithium.service.user.client.objects.Domain;
import lithium.service.user.client.objects.LoginEvent;
import lithium.service.user.client.objects.User;
import lithium.service.user.client.service.UserApiInternalClientService;
import org.mockito.Mockito;


public class GamePlayPhase1TestNegativeMocks {

    public static GamePlayRepository mockGamePlayRepository() {
        GamePlayRepository gamePlayRepository = Mockito.mock(GamePlayRepository.class);
        Mockito.when(gamePlayRepository.findByGuid(Mockito.anyString()))
                .thenReturn(null);
        return gamePlayRepository;
    }

    public static ValidationHelper mockValidationHelper() throws Status500RuntimeException, Status401NotLoggedInException, Status512ProviderNotConfiguredException, Status400BadRequestException, Status406DisabledGameException {
        lithium.service.user.client.objects.User user = User.builder().guid("123").build();
        lithium.service.domain.client.objects.Domain domain = lithium.service.domain.client.objects.Domain.builder().name("livescore_uganda").build();
        LoginEvent lastLoginEvent = LoginEvent.builder().user(user)
                .domain(Domain.builder().name("Heins_Domain").build())
                .build();
        ValidationHelper validationHelper = Mockito.mock(ValidationHelper.class);
        Mockito.when(validationHelper.findLastLoginEventForSessionKey(new GamePlayContext(), Mockito.anyString())).thenReturn(lastLoginEvent);
        Mockito.when(validationHelper.getDomain(new GamePlayContext(), Mockito.anyString())).thenReturn(domain);
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

    public static UserApiInternalClientService mockUserApiInternalClientService() throws Status500RuntimeException, Status401NotLoggedInException, Status500UserInternalSystemClientException, Status405UserDisabledException, Status401UnAuthorisedException {
        UserApiInternalClientService userApiInternalClientService = Mockito.mock(UserApiInternalClientService.class);
        Mockito.when(userApiInternalClientService.performUserChecks(Mockito.anyString(), Mockito.anyString(),
                Mockito.anyLong(), Mockito.anyBoolean(), Mockito.anyBoolean(), Mockito.anyBoolean())).thenReturn(null);
        return userApiInternalClientService;
    }

    public static LimitInternalSystemService mockLimitInternalSystemService() throws Status496PlayerCoolingOffException, Status490SoftSelfExclusionException, Status500LimitInternalSystemClientException, Status491PermanentSelfExclusionException {
        LimitInternalSystemService limits = Mockito.mock(LimitInternalSystemService.class);
        Mockito.doNothing().when(limits).checkPlayerRestrictions(Mockito.anyString(), Mockito.anyString());
        return limits;
    }
}
