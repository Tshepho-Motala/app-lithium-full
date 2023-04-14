package lithium.service.casino.provider.roxor.util;

import lithium.modules.ModuleInfo;
import lithium.service.Response;
import lithium.service.casino.exceptions.Status512ProviderNotConfiguredException;
import lithium.service.casino.provider.roxor.api.exceptions.Status400BadRequestException;
import lithium.service.casino.provider.roxor.api.exceptions.Status401NotLoggedInException;
import lithium.service.casino.provider.roxor.api.exceptions.Status406DisabledGameException;
import lithium.service.casino.provider.roxor.api.exceptions.Status500RuntimeException;
import lithium.service.casino.provider.roxor.config.ProviderConfig;
import lithium.service.casino.provider.roxor.config.ProviderConfigService;
import lithium.service.casino.provider.roxor.context.GamePlayContext;
import lithium.service.casino.provider.roxor.storage.entities.User;
import lithium.service.casino.provider.roxor.storage.repositories.GamePlayRequestRepository;
import lithium.service.casino.provider.roxor.storage.repositories.OperationRepository;
import lithium.service.casino.provider.roxor.storage.repositories.UserRepository;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.domain.client.CachingDomainClientService;
import lithium.service.domain.client.objects.Domain;
import lithium.service.games.client.GamesClient;
import lithium.service.games.client.exceptions.Status411GameNotFoundException;
import lithium.service.games.client.objects.Game;
import lithium.service.user.client.exceptions.Status412LoginEventNotFoundException;
import lithium.service.user.client.objects.LoginEvent;
import lithium.service.user.client.system.SystemLoginEventsClient;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

@Slf4j
@Service
public class ValidationHelper {
    @Autowired @Setter ModuleInfo moduleInfo;
    @Autowired @Setter LithiumServiceClientFactory services;
    @Autowired @Setter ProviderConfigService providerConfigService;
    @Autowired @Setter CachingDomainClientService cachingDomainClientService;
    @Autowired @Setter GamePlayRequestRepository gamePlayRequestRepository;
    @Autowired @Setter OperationRepository operationRepository;
    @Autowired @Setter UserRepository userRepository;


    public Domain getDomain(
            GamePlayContext context,
            String domainName
    ) throws
            Status500RuntimeException
    {
        try {
            Domain domain = cachingDomainClientService.retrieveDomainFromDomainService(domainName);
            return domain;
        } catch (Exception e) {
            log.error("Could not retrieve domain/website details for domain : " + domainName, e);
            context.setGamePlayRequestErrorReason("Could not retrieve domain/website details for domain : " + domainName);
            throw new Status500RuntimeException(context);
        }
    }

    public lithium.service.games.client.objects.Game getGame(
            GamePlayContext context,
            String domainName,
            String providerGuid,
            String providerGameId
    ) throws
            Status400BadRequestException,
            Status406DisabledGameException,
            Status500RuntimeException {
        try {
            GamesClient gamesClient = services.target(GamesClient.class, "service-games", true);
            Response<Game> gameResponse = gamesClient.findByGuidAndDomainNameNoLabels(domainName, providerGuid+"_"+providerGameId);
            if (gameResponse.getStatus().equals(Response.Status.DISABLED)) {
                throw new Status406DisabledGameException("Game Disabled");
            }
            if (gameResponse.isSuccessful() && gameResponse.getData() != null) {
                return gameResponse.getData();
            }

            throw new Status411GameNotFoundException("Could not retrieve game details for domainName : " + domainName +
                    " providerGuid : " + providerGuid + " providerGameId : " + providerGameId);
        } catch (Status406DisabledGameException e) {
            log.debug("Could not retrieve game details for disabled game : " + domainName +
                    " providerGuid : " + providerGuid + " providerGameId : " + providerGameId, e);
            context.setGamePlayRequestErrorReason("Could not retrieve game details for disabled game : " + domainName +
                    " providerGuid : " + providerGuid + " providerGameId : " + providerGameId);
            throw new Status406DisabledGameException("Game Disabled");
        } catch (Status411GameNotFoundException e) {
            log.debug("Could not retrieve game details for domainName : " + domainName +
                    " providerGuid : " + providerGuid + " providerGameId : " + providerGameId, e);
            context.setGamePlayRequestErrorReason("Could not retrieve game details for domainName : " + domainName +
                    " providerGuid : " + providerGuid + " providerGameId : " + providerGameId);
            throw new Status400BadRequestException(context);
        } catch (Exception e) {
            log.debug("Unhandled error while trying to retrieve game details for domainName : " + domainName +
                    " providerGuid : " + providerGuid + " providerGameId : " + providerGameId, e);
            context.setGamePlayRequestErrorReason("Unhandled error while trying to retrieve game details for domainName : " + domainName +
                    " providerGuid : " + providerGuid + " providerGameId : " + providerGameId);
            throw new Status500RuntimeException(context);
        }
    }

    public lithium.service.games.client.objects.Game getGame(
            GamePlayContext context,
            String domainName,
            String gameGuid
    ) throws
            Status400BadRequestException,
            Status500RuntimeException
    {
        try {
            GamesClient gamesClient = services.target(GamesClient.class, "service-games", true);
            Response<Game> gameResponse = gamesClient.findByGuidAndDomainName(domainName, gameGuid);
            if (gameResponse.isSuccessful() && gameResponse.getData() != null) {
                return gameResponse.getData();
            }

            throw new Status411GameNotFoundException("Could not retrieve game details for domain: " + domainName +
                    " gameGuid : " + gameGuid);
        } catch (Status411GameNotFoundException e) {
            log.error("Could not retrieve game details for domain: " + domainName + " gameGuid : " + gameGuid, e);
            context.setGamePlayRequestErrorReason("Could not retrieve game details for domain: " + domainName +
                    " gameGuid : " + gameGuid);
            throw new Status400BadRequestException(context);
        } catch (Exception e) {
            log.error("Unhandled error while trying to retrieve game details for domain: " + domainName +
                    " gameGuid : " + gameGuid, e);
            context.setGamePlayRequestErrorReason("Unhandled error while trying to retrieve game details for " +
                    "domain: " + domainName + " gameGuid : " + gameGuid);
            throw new Status500RuntimeException(context);
        }
    }

    public LoginEvent findLastLoginEventForSessionKey(
            GamePlayContext context,
            String sessionKey
    ) throws
            Status401NotLoggedInException,
            Status500RuntimeException
    {
        try {
            if (sessionKey == null) {
                return null;
            }

            SystemLoginEventsClient systemLoginEventsClient = services.target(SystemLoginEventsClient.class, "service-user", true);
            LoginEvent loginEvent = systemLoginEventsClient.findBySessionKey(sessionKey);

            if (loginEvent.getLogout() != null
                    && loginEvent.getLogout().before(Calendar.getInstance().getTime())
            ) {
                context.setGamePlayRequestErrorReason("Session : " + sessionKey + " ended : " + loginEvent.getLogout());
                throw new Status401NotLoggedInException(context);
            }

            return loginEvent;
        } catch (Status401NotLoggedInException exception) {
            log.error("Session already logged out : " + sessionKey);
            throw exception;
        } catch (Status412LoginEventNotFoundException exception) {
            log.error("Could not retrieve session for sessionKey : " + sessionKey);
            context.setGamePlayRequestErrorReason("Could not retrieve session for sessionKey : " + sessionKey);
            throw new Status401NotLoggedInException(context);
        } catch (Exception exception) {
            log.error("Could not retrieve session info.", exception);
            context.setGamePlayRequestErrorReason("Could not retrieve session for sessionKey : " + sessionKey);
            throw new Status500RuntimeException(context);
        }
    }

    public void validateIpWhiteList(
            ProviderConfig pc,
            String domainName,
            String xForwardFor,
            GamePlayContext context
    ) throws
            Status500RuntimeException,
            Status512ProviderNotConfiguredException
    {
        if (pc == null) {
            pc = providerConfigService.getConfig(moduleInfo.getModuleName(), domainName);
        }

        if (!verifyIPAddress(pc.getIpWhiteList(), xForwardFor)) {
            String message = "IP WhiteList Failure, supported List : " + pc.getIpWhiteList() +
                    " supplied, X-Forward-For : " + xForwardFor;

            log.info(message);
            context.setGamePlayRequestErrorReason(message);
            throw new Status500RuntimeException(context);
        }
    }

    public void validateUser(
            String playerUser,
            String loginEventUser,
            GamePlayContext context
    ) throws
            Status401NotLoggedInException
    {
        if (!loginEventUser.equalsIgnoreCase(playerUser)) {
            String message = "loginEventUser : " + loginEventUser +
                    " does not match request playerId : " + playerUser;

            log.warn(message);
            context.setGamePlayRequestErrorReason(message);
            throw new Status401NotLoggedInException(context);
        }
    }

    public void validateWebsite(
            ProviderConfig pc,
            String domainName,
            String website,
            GamePlayContext context
    ) throws
            Status401NotLoggedInException,
            Status512ProviderNotConfiguredException
    {
        if (pc == null) {
            pc = providerConfigService.getConfig(moduleInfo.getModuleName(), domainName);
        }

        if (!pc.getWebsite().equalsIgnoreCase(website)) {
            String message = "Provider Website Property : " + pc.getWebsite() +
                    " does not match request website : " + website;

            log.info(message);
            context.setGamePlayRequestErrorReason(message);
            throw new Status401NotLoggedInException(context);
        }
    }

    public Boolean verifyIPAddress(String ipWhiteList, String xForwardFor) {
        if (xForwardFor != null && !xForwardFor.trim().isEmpty()) {
            List<String> xForwardForList = Arrays.asList(xForwardFor.split(",").clone());
            for (String xForwardForItem : xForwardForList) {
                if (ipWhiteList.contains(xForwardForItem)) {
                    return Boolean.TRUE;
                }
            }
        }
        return Boolean.FALSE;
    }

    public void validate(GamePlayContext context,
                         LoginEvent loginEvent,
                         String playerGuid,
                         String website,
                         String xForwardFor
    ) throws
            Status401NotLoggedInException,
            Status512ProviderNotConfiguredException,
            Status500RuntimeException
    {
        if (loginEvent != null) {
            validate(context, playerGuid, website, xForwardFor,
                    loginEvent.getUser().getGuid(), loginEvent.getDomain().getName()
            );
        } else {
            validate(context, playerGuid, website, xForwardFor,
                    null, getDomainNameFromPlayerGuid(playerGuid));
        }
    }

    private void validate(
            GamePlayContext context,
            String playerGuid,
            String website,
            String xForwardFor,
            String loginEventUserGuid,
            String domainName

    ) throws
            Status401NotLoggedInException,
            Status512ProviderNotConfiguredException,
            Status500RuntimeException
    {
        if (loginEventUserGuid != null && playerGuid != null) { // FIXME: https://jira.livescore.com/browse/PLAT-1144
            validateUser(
                    playerGuid,
                    loginEventUserGuid,
                    context
            );
        }

        //validate provider website matches request website
        ProviderConfig pc = providerConfigService.getConfig(moduleInfo.getModuleName(), domainName);

        if (website != null) {
            validateWebsite(
                    pc,
                    domainName,
                    website,
                    context
            );
        }

        //validate IP White List
        if (pc.getIpWhiteList() != null && !pc.getIpWhiteList().trim().isEmpty()) {
            validateIpWhiteList(
                    pc,
                    domainName,
                    xForwardFor,
                    context
            );
        }
    }

    public String getUserGuidFromApiToken(String apiToken) {
        User user = userRepository.findByApiToken(apiToken);
        if (user != null) {
            return user.getGuid();
        }

        return null;
    }

    public String getUserApiTokenFromGuid(String userGuid) {
        User user = userRepository.findByGuid(userGuid);
        if (user != null) {
            return user.getApiToken();
        }

        return null;
    }

    public String getDomainNameFromPlayerGuid(final String playerGuid) {
        return playerGuid.split("/", 2)[0];
    }
}
