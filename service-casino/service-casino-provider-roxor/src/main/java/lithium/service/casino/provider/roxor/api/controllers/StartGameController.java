package lithium.service.casino.provider.roxor.api.controllers;

import lithium.service.Response;
import lithium.service.Response.Status;
import lithium.service.casino.client.Mockable;
import lithium.service.casino.exceptions.Status512ProviderNotConfiguredException;
import lithium.service.casino.provider.roxor.api.exceptions.Status401NotLoggedInException;
import lithium.service.casino.provider.roxor.api.exceptions.Status500RuntimeException;
import lithium.service.casino.provider.roxor.services.GameListService;
import lithium.service.casino.provider.roxor.services.StartGameService;
import lithium.service.client.datatable.DataTableResponse;
import lithium.service.domain.client.exceptions.Status550ServiceDomainClientException;
import lithium.service.domain.client.util.LocaleContextProcessor;
import lithium.service.games.client.GamesClient;
import lithium.service.games.client.exceptions.Status429UserLoggedOutException;
import lithium.service.games.client.exceptions.Status501NotImplementedException;
import lithium.service.games.client.exceptions.Status502ProviderProcessingException;
import lithium.service.games.client.objects.Game;
import lithium.service.games.client.objects.GameUserStatus;
import lithium.service.games.client.objects.User;
import lithium.service.limit.client.exceptions.Status483PlayerCasinoNotAllowedException;
import lithium.service.limit.client.exceptions.Status500LimitInternalSystemClientException;
import lithium.util.ExceptionMessageUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.UnsupportedEncodingException;
import java.util.List;

@Slf4j
@RestController
public class StartGameController extends Mockable implements GamesClient {
    @Autowired StartGameService startGameService;
    @Autowired GameListService gameListService;
    @Autowired LocaleContextProcessor localeContextProcessor;

    @Override
    @RequestMapping("/games/{domainName}/startGame")
    public Response<String> startGame(
            @PathVariable("domainName") String domainName,
            @RequestParam("token") String token,
            @RequestParam("gameId") String gameId,
            @RequestParam("lang") String lang,
            @RequestParam("currency") String currency,
            @RequestParam(value = "os", required = false) String os,
            @RequestParam(value = "machineGUID", required = false) String machineGUID,
            @RequestParam(value = "tutorial", required = false) Boolean tutorial,
            @RequestParam(value = "platform", required = false, defaultValue = "desktop") String platform
    ) throws
            Status429UserLoggedOutException,
            Status483PlayerCasinoNotAllowedException,
            Status500LimitInternalSystemClientException,
            Status502ProviderProcessingException,
            Status512ProviderNotConfiguredException,
            Status550ServiceDomainClientException
    {
        localeContextProcessor.setLocaleContextHolder(lang, domainName);
        try {
            log.info("startGame request with domainName : " + domainName + " gameId : " + gameId +
                    " lang : " + lang + " currency : " + currency + " os : " + os);

            String returnUrl = startGameService.startGame(
                    domainName,
                    token,
                    gameId,
                    lang,
                    currency,
                    Boolean.FALSE,
                    platform
            );

            log.info("startGame response domainName : " + domainName + " gameId : " + gameId +
                    " returnUrl : " + returnUrl);

            return Response.<String>builder().data(returnUrl).status(Status.OK).build();
        } catch (Status401NotLoggedInException e) {
            log.error("start-game userToken no longer valid exception " + ExceptionMessageUtil.allMessages(e), e);
            throw new Status429UserLoggedOutException();
        } catch (Status483PlayerCasinoNotAllowedException e) {
            log.error("start-game service-limit player casino not allowed exception ["
                    + "DomainName" + domainName + ", "
                    + "GameId" + gameId +"] "
                    + ExceptionMessageUtil.allMessages(e), e);
            throw e;
        } catch (Status500LimitInternalSystemClientException e) {
            log.error("start-game service-limit Internal Exception ["
                    + "DomainName" + domainName + ", "
                    + "GameId" + gameId +"] "
                    + ExceptionMessageUtil.allMessages(e), e);
            throw new Status500LimitInternalSystemClientException(e);
        } catch (Status512ProviderNotConfiguredException e) {
            log.error("start-game Provider Not Configured Exception ["
                    + "DomainName" + domainName + ", "
                    + "GameId" + gameId +"] "
                    + ExceptionMessageUtil.allMessages(e), e);
            throw new Status512ProviderNotConfiguredException(domainName);
        } catch (Status500RuntimeException e) {
            log.error("start-game RuntimeException ["
                    + "DomainName" + domainName + ", "
                    + "GameId" + gameId +"] "
                    + ExceptionMessageUtil.allMessages(e), e);
            throw new Status550ServiceDomainClientException("Unable to retrieve domain from domain service : " + domainName);
        } catch (UnsupportedEncodingException e) {
            log.error("start-game URL encoding exception ["
                    + "DomainName" + domainName + ", "
                    + "GameId" + gameId +"] "
                    + ExceptionMessageUtil.allMessages(e), e);
            throw new Status502ProviderProcessingException("ProviderRoxor processing exception : " + e.getMessage());
        }
    }

    @Override
    @RequestMapping("/games/{domainName}/demoGame")
    public Response<String> demoGame(
            @PathVariable("domainName") String domainName,
            @RequestParam("gameId") String gameId,
            @RequestParam("lang") String lang,
            @RequestParam(value = "os", required = false) String os
    ) throws
            Status429UserLoggedOutException,
            Status483PlayerCasinoNotAllowedException,
            Status500LimitInternalSystemClientException,
            Status502ProviderProcessingException,
            Status512ProviderNotConfiguredException,
            Status550ServiceDomainClientException
    {
        try {
            log.info("demoGame request with domainName : " + domainName + " gameId : " + gameId +
                    " lang : " + lang + " os : " + os);

            String returnUrl = startGameService.startGame(
                    domainName,
                    null,
                    gameId,
                    lang,
                    null,
                    Boolean.TRUE,
                    "desktop"
            );

            log.info("demoGame response with domainName : " + domainName + " gameId : " + gameId +
                    " returnUrl " + returnUrl);

            return Response.<String>builder().data(returnUrl).status(Status.OK).build();
        } catch (Status401NotLoggedInException e) {
            log.error("demo-game userToken no longer valid exception " + ExceptionMessageUtil.allMessages(e), e);
            throw new Status429UserLoggedOutException();
        } catch (Status483PlayerCasinoNotAllowedException e) {
            log.error("demo-game service-limit player casino not allowed exception ["
                    + "DomainName" + domainName + ", "
                    + "GameId" + gameId +"] "
                    + ExceptionMessageUtil.allMessages(e), e);
            throw e;
        } catch (Status500LimitInternalSystemClientException e) {
            log.error("demo-game service-limit Internal Exception ["
                    + "DomainName" + domainName + ", "
                    + "GameId" + gameId +"] "
                    + ExceptionMessageUtil.allMessages(e), e);
            throw new Status500LimitInternalSystemClientException(e);
        } catch (Status512ProviderNotConfiguredException e) {
            log.error("demo-game Provider Not Configured Exception ["
                    + "DomainName" + domainName + ", "
                    + "GameId" + gameId +"] "
                    + ExceptionMessageUtil.allMessages(e), e);
            throw new Status512ProviderNotConfiguredException(domainName);
        } catch (Status500RuntimeException e) {
            log.error("demo-game RuntimeException ["
                    + "DomainName" + domainName + ", "
                    + "GameId" + gameId +"] "
                    + ExceptionMessageUtil.allMessages(e), e);
            throw new Status550ServiceDomainClientException("Unable to retrieve domain from domain service : " + domainName);
        } catch (UnsupportedEncodingException e) {
            log.error("demo-game URL encoding exception ["
                    + "DomainName" + domainName + ", "
                    + "GameId" + gameId +"] "
                    + ExceptionMessageUtil.allMessages(e), e);
            throw new Status502ProviderProcessingException("ProviderRoxor processing exception : " + e.getMessage());
        }
    }

    @Override
    @RequestMapping("/games/{gameId}/unlock/toggle")
    public Response<GameUserStatus> toggleLocked(
            @PathVariable("gameId") Long gameId,
            @RequestBody User user) {
        return Response.<GameUserStatus>builder().status(Status.NOT_IMPLEMENTED).build();
    }

    @Override
    @RequestMapping("/games/{gameGuid}/unlock")
    public Response<GameUserStatus> unlock(@PathVariable("gameGuid") String gameGuid,
                                           @RequestBody User user) {
        return Response.<GameUserStatus>builder().status(Status.NOT_IMPLEMENTED).build();
    }

    @Override
    @RequestMapping("/games/{domainName}/listGames")
    public List<Game> listGames(@PathVariable("domainName") String domainName) throws Exception {
        log.info("listGames for domain : " + domainName);

        List<Game> gameList = gameListService.getGameList(domainName);

        log.info("listGames for domain : " + domainName + " response : " + gameList);

        return gameList;
    }

    @Override
    @RequestMapping("/games/{domainName}/listFrbGames")
    public List<Game> listFrbGames(@PathVariable("domainName") String domainName) throws Exception {
        throw new Status501NotImplementedException();
    }

    @Override
    @RequestMapping("/games/add")
    public Response<Game> addGame(
            @RequestParam("providerGuid")String providerGuid,
            @RequestParam("providerGameId") String providerGameId,
            @RequestParam("gameName") String gameName) throws Exception {
        throw new Status501NotImplementedException();
    }

    @Override
    @RequestMapping("/games/{gameId}/findById")
    public Response<Game> findById(@PathVariable("gameId") Long gameId) throws Exception {
        throw new Status501NotImplementedException();
    }

    @Override
    @RequestMapping("/games/{gameId}/editGraphic/{graphicFunction}")
    public Response<Game> editGraphic(
            @PathVariable("gameId") Long gameId,
            @PathVariable("graphicFunction") String graphicFunction,
            @RequestParam("file") MultipartFile file) throws Exception {
        throw new Status501NotImplementedException();
    }

    @Override
    @RequestMapping("/games/edit")
    public Response<Game> edit(@RequestBody Game game) throws Exception {
        throw new Status501NotImplementedException();
    }

    @Override
    @RequestMapping("/games/{domainName}/listDomainGames")
    public Response<Iterable<Game>> listDomainGames(@PathVariable("domainName") String domainName) throws Exception {
        throw new Status501NotImplementedException();
    }

    @Override
    @RequestMapping("/games/{domainName}/listDomainGamesPerChannel")
    public List<Game> listDomainGamesPerChannel(
            @PathVariable("domainName") String domainName,
            @RequestParam("channel") String channel,
            @RequestParam(name = "enabled", required = true) Boolean enabled,
            @RequestParam(name = "visible", required = true) Boolean visible
    ) throws Exception {
        throw new Status501NotImplementedException();
    }

    @Override
    @RequestMapping("/games/{domainName}/find/guid/{gameGuid}")
    public Response<lithium.service.games.client.objects.Game> findByGuidAndDomainName(
            @PathVariable("domainName") String domainName,
            @PathVariable("gameGuid") String gameGuid
    ) throws Exception {
        throw new Status501NotImplementedException();
    }

    @RequestMapping("/games/{domainName}/find/guid/{gameGuid}/no-labels")
    public Response<lithium.service.games.client.objects.Game> findByGuidAndDomainNameNoLabels(
            @PathVariable("domainName") String domainName,
            @PathVariable("gameGuid") String gameGuid
    ) throws Exception {
        throw new Status501NotImplementedException();
    }

    @Override
    @RequestMapping("/games/{domainName}/listDomainGamesDT")
    public DataTableResponse<Game> listDomainGames(
            @PathVariable("domainName") String domainName,
            @RequestParam(name="enabled", defaultValue="true") Boolean enabled,
            @RequestParam("draw") String drawEcho,
            @RequestParam("start") Long start,
            @RequestParam("length") Long length) {
        return new DataTableResponse<Game>();
    }

    @Override
    @RequestMapping("/games/{domainName}/listDomainGamesReport")
    public DataTableResponse<Game> listDomainGamesReport(
            @PathVariable("domainName") String domainName,
            @RequestParam("draw") String drawEcho,
            @RequestParam("start") Long start,
            @RequestParam("length") Long length) {
        return new DataTableResponse<Game>();
    }

    @Override
    @RequestMapping("/games/{domainName}/isGameLockedForPlayer")
    public Response<Boolean> isGameLockedForPlayer(
            @PathVariable("domainName") String domainName,
            @RequestParam("gameGuid") String gameGuid,
            @RequestParam("playerGuid") String playerGuid) {
        return Response.<Boolean>builder().status(Status.NOT_IMPLEMENTED).build();
    }
}
