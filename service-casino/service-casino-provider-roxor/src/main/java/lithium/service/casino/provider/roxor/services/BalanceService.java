package lithium.service.casino.provider.roxor.services;

import lithium.metrics.TimeThisMethod;
import lithium.modules.ModuleInfo;
import lithium.service.casino.CasinoClientService;
import lithium.service.casino.provider.roxor.api.exceptions.Status400BadRequestException;
import lithium.service.casino.provider.roxor.api.exceptions.Status401NotLoggedInException;
import lithium.service.casino.provider.roxor.api.exceptions.Status500RuntimeException;
import lithium.service.casino.provider.roxor.api.schema.Money;
import lithium.service.casino.provider.roxor.api.schema.SuccessResponse;
import lithium.service.casino.provider.roxor.api.schema.SuccessStatus;
import lithium.service.casino.provider.roxor.api.schema.balance.GetPlayerBalanceRequest;
import lithium.service.casino.provider.roxor.config.ProviderConfigService;
import lithium.service.casino.provider.roxor.context.GamePlayContext;
import lithium.service.casino.provider.roxor.util.ValidationHelper;
import lithium.service.domain.client.objects.Domain;
import lithium.service.limit.client.LimitInternalSystemService;
import lithium.service.user.client.objects.LoginEvent;
import lithium.util.ExceptionMessageUtil;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class BalanceService {
    @Autowired @Setter CasinoClientService casinoService;
    @Autowired @Setter LimitInternalSystemService limits;
    @Autowired @Setter ValidationHelper validationHelper;
    @Autowired @Setter ProviderConfigService providerConfigService;
    @Autowired @Setter ModuleInfo moduleInfo;

    @TimeThisMethod
    public SuccessResponse balance(
            String sessionKey, String xForwardFor,
            GetPlayerBalanceRequest playerBalanceRequest
    ) throws
            Status400BadRequestException,
            Status401NotLoggedInException,
            Status500RuntimeException
    {
        try {
            if (sessionKey == null) throw new Status400BadRequestException();
            if (playerBalanceRequest == null) throw new Status400BadRequestException();
            if (playerBalanceRequest.getPlayerId() == null) throw new Status400BadRequestException();
            if (playerBalanceRequest.getWebsite() == null) throw new Status400BadRequestException();
            // if (playerBalanceRequest.getGameKey() == null) throw new Status400BadRequestException();

            String userGuid = validationHelper.getUserGuidFromApiToken(playerBalanceRequest.getPlayerId());
            if (userGuid == null) {
                throw new Status401NotLoggedInException();
            }

            LoginEvent lastLoginEvent = validationHelper.findLastLoginEventForSessionKey(new GamePlayContext(), sessionKey);
            if (lastLoginEvent != null) {
                //validate user session matches authenticate request user
                validationHelper.validate(
                        new GamePlayContext(),
                        lastLoginEvent,
                        userGuid,
                        playerBalanceRequest.getWebsite(),
                        xForwardFor
                );

                Domain domain = validationHelper.getDomain(new GamePlayContext(), lastLoginEvent.getDomain().getName());

                Long balanceCents = casinoService.getPlayerBalance(
                        domain.getName(),
                        userGuid,
                        domain.getCurrency()
                ).getBalanceCents();

                SuccessResponse successResponse = SuccessResponse.builder()
                        .status(SuccessStatus.builder().code(SuccessStatus.Code.OK).build())
                        .balance(Money.builder()
                                .currency(domain.getCurrency())
                                .amount(balanceCents)
                                .build()
                        ).build();

                return successResponse;
            } else {
                throw new Status401NotLoggedInException();
            }
        } catch (Status400BadRequestException | Status401NotLoggedInException e) {
            log.warn("player-balance [sessionKey="+sessionKey+", xForwardFor="+xForwardFor
                    +", playerBalanceRequest="+playerBalanceRequest+"] " + e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("player-balance [sessionKey="+sessionKey+", xForwardFor="+xForwardFor
                    +", playerBalanceRequest="+playerBalanceRequest+"] " +
                    ExceptionMessageUtil.allMessages(e), e);
            throw new Status500RuntimeException();
        }
    }
}
