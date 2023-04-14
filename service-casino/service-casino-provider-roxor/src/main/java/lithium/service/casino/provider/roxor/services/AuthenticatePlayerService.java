package lithium.service.casino.provider.roxor.services;

import lithium.modules.ModuleInfo;
import lithium.service.casino.provider.roxor.api.exceptions.Status400BadRequestException;
import lithium.service.casino.provider.roxor.api.exceptions.Status401NotLoggedInException;
import lithium.service.casino.provider.roxor.api.exceptions.Status500RuntimeException;
import lithium.service.casino.provider.roxor.api.schema.Player;
import lithium.service.casino.provider.roxor.api.schema.SuccessStatus;
import lithium.service.casino.provider.roxor.api.schema.auth.AuthenticatePlayerRequest;
import lithium.service.casino.provider.roxor.api.schema.auth.AuthenticatePlayerResponse;
import lithium.service.casino.provider.roxor.config.ProviderConfigService;
import lithium.service.casino.provider.roxor.context.GamePlayContext;
import lithium.service.casino.provider.roxor.util.ValidationHelper;
import lithium.service.user.client.objects.LoginEvent;
import lithium.util.ExceptionMessageUtil;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AuthenticatePlayerService {
    @Autowired ProviderConfigService providerConfigService;
    @Autowired ModuleInfo moduleInfo;
    @Autowired @Setter ValidationHelper validationHelper;

    public AuthenticatePlayerResponse authenticatePlayer(
            String sessionKey,
            String xForwardFor,
            AuthenticatePlayerRequest authenticatePlayerRequest
    ) throws
            Status400BadRequestException,
            Status401NotLoggedInException,
            Status500RuntimeException
    {
        try {
            //validate input
            if (sessionKey == null) throw new Status400BadRequestException();
            if (authenticatePlayerRequest == null) throw new Status400BadRequestException();
            if (authenticatePlayerRequest.getPlayerId() == null) throw new Status400BadRequestException();
            if (authenticatePlayerRequest.getWebsite() == null) throw new Status400BadRequestException();

            String userGuid = validationHelper.getUserGuidFromApiToken(authenticatePlayerRequest.getPlayerId());
            if (userGuid == null) {
                throw new Status401NotLoggedInException();
            }

            String screenName = null;
            //validate SessionKey
            LoginEvent lastLoginEvent = validationHelper.findLastLoginEventForSessionKey(new GamePlayContext(), sessionKey);
            if (lastLoginEvent != null) {
                //validate user session matches authenticate request user
                validationHelper.validate(
                        new GamePlayContext(),
                        lastLoginEvent,
                        userGuid,
                        authenticatePlayerRequest.getWebsite(),
                        xForwardFor
                );

                screenName = lastLoginEvent.getUser().getUsername();
            } else {
                throw new Status401NotLoggedInException();
            }

            return AuthenticatePlayerResponse.builder()
                    .player(Player.builder()
                            .playerId(authenticatePlayerRequest.getPlayerId())
                            .website(authenticatePlayerRequest.getWebsite())
                            .screenName(screenName)
                            .build()
                    )
                    .status(SuccessStatus.builder()
                            .code(SuccessStatus.Code.OK)
                            .build()
                    ).build();
        } catch (Status400BadRequestException | Status401NotLoggedInException e) {
            log.warn("authenticate-player [sessionKey="+sessionKey+", xForwardFor="+xForwardFor
                    +", authenticatePlayerRequest="+authenticatePlayerRequest+"] " + e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("authenticate-player [sessionKey="+sessionKey+", xForwardFor="+xForwardFor
                    +", authenticatePlayerRequest="+authenticatePlayerRequest+"] " +
                    ExceptionMessageUtil.allMessages(e), e);
            throw new Status500RuntimeException();
        }
    }
}
