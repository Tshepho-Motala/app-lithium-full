package lithium.service.casino.provider.iforium.controller;

import lithium.service.Response;
import lithium.service.casino.exceptions.Status512ProviderNotConfiguredException;
import lithium.service.casino.provider.iforium.exception.InternalServerErrorException;
import lithium.service.casino.provider.iforium.service.ListGameService;
import lithium.service.casino.provider.iforium.service.StartGameService;
import lithium.service.client.LithiumServiceClientFactoryException;
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
import lithium.service.user.client.exceptions.Status411UserNotFoundException;
import lithium.service.user.client.exceptions.UserClientServiceFactoryException;
import lithium.service.user.client.exceptions.UserNotFoundException;
import lithium.util.ExceptionMessageUtil;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.UnsupportedEncodingException;
import java.util.List;

import static java.lang.String.format;
import static lithium.service.casino.provider.iforium.util.RequestUtils.playMode;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ServiceGameController implements GamesClient {

    private final StartGameService startGameService;
    private final ListGameService listGameService;
    @Autowired @Setter
    LocaleContextProcessor localeContextProcessor;

    @Override
    public Response<String> startGame(
            @PathVariable("domainName") String domainName,
            @RequestParam("token") String token,
            @RequestParam("gameId") String gameId,
            @RequestParam(value = "lang") String lang,
            @RequestParam(value = "currency") String currency,
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
            Status550ServiceDomainClientException {
        localeContextProcessor.setLocaleContextHolder(lang, domainName);
        return startGame(domainName, gameId, token, false, platform, lang);
    }

    @Override
    public Response<String> demoGame(
            @PathVariable("domainName") String domainName,
            @RequestParam("gameId") String gameId,
            @RequestParam(value = "lang") String lang,
            @RequestParam(value = "os") String os
    ) throws Status512ProviderNotConfiguredException, Status550ServiceDomainClientException, Status483PlayerCasinoNotAllowedException {
        return startGame(domainName, gameId, null, true, "desktop", lang);
    }

    private Response<String> startGame(String domainName, String gameId, String token, boolean demo, String platform, String lang) throws
            Status512ProviderNotConfiguredException,
            Status550ServiceDomainClientException {
        try {
            log.info(format("Iforium %s request with domainName: %s, gameId: %s", playMode(demo), domainName, gameId));

            String responseUrl = startGameService.startGame(token, gameId, domainName, demo, platform, lang);

            log.info(format("%s response domainName: %s, gameId: %s, responseUrl: %s", playMode(demo), domainName, gameId,
                    responseUrl));
            return Response.<String>builder().data(responseUrl).build();
        }catch (Status483PlayerCasinoNotAllowedException status483PlayerCasinoNotAllowedException){
            log.debug("start-game player casino not allowed exception ["
            + "DomainName " + domainName
            + ", GameId " + gameId + "] "
            + ExceptionMessageUtil.allMessages(status483PlayerCasinoNotAllowedException), status483PlayerCasinoNotAllowedException);
            throw status483PlayerCasinoNotAllowedException;
        } catch (UnsupportedEncodingException | Status411UserNotFoundException | LithiumServiceClientFactoryException
                | UserClientServiceFactoryException | UserNotFoundException exception) {
            String message = format("%s URL exception [domainName: %s, gameId: %s] %s", playMode(demo), domainName, gameId,
                                    ExceptionMessageUtil.allMessages(exception));
            log.error(message, exception);
            throw new InternalServerErrorException(message);
        }
    }

    @Override
    public List<Game> listGames(@PathVariable("domainName") String domainName) throws Status512ProviderNotConfiguredException {
        return listGameService.listGames(domainName);
    }

    @Override
    public Response<GameUserStatus> toggleLocked(Long gameId, User user) {
        return Response.<GameUserStatus>builder().status(Response.Status.NOT_IMPLEMENTED).build();
    }

    @Override
    public Response<GameUserStatus> unlock(String gameGuid, User user) {
        return Response.<GameUserStatus>builder().status(Response.Status.NOT_IMPLEMENTED).build();
    }

    @Override
    public List<Game> listFrbGames(String domainName) throws Exception {
        throw new Status501NotImplementedException();
    }

    @Override
    public Response<Game> addGame(String providerGuid, String providerGameId, String gameName) throws Exception {
        throw new Status501NotImplementedException();
    }

    @Override
    public Response<Game> findById(@PathVariable("gameId") Long gameId) throws Exception {
        throw new Status501NotImplementedException();
    }

    @Override
    public Response<Game> editGraphic(@PathVariable("gameId") Long gameId, @PathVariable("graphicFunction") String graphicFunction,
                                      MultipartFile file) throws Exception {
        throw new Status501NotImplementedException();
    }

    @Override
    public Response<Game> edit(Game game) throws Exception {
        throw new Status501NotImplementedException();
    }

    @Override
    public Response<Iterable<Game>> listDomainGames(String domainName) throws Exception {
        throw new Status501NotImplementedException();
    }

    @Override
    public List<Game> listDomainGamesPerChannel(String domainName, String channel, Boolean enabled, Boolean visible) throws Exception {
        throw new Status501NotImplementedException();
    }

    @Override
    public Response<Game> findByGuidAndDomainName(String domainName, String gameGuid) throws Exception {
        throw new Status501NotImplementedException();
    }

    @Override
    public Response<Game> findByGuidAndDomainNameNoLabels(String domainName, String gameGuid) throws Exception {
        throw new Status501NotImplementedException();
    }

    @Override
    public DataTableResponse<Game> listDomainGames(String domainName, Boolean enabled, String drawEcho, Long start, Long length) {
        return new DataTableResponse<>();
    }

    @Override
    public DataTableResponse<Game> listDomainGamesReport(String domainName, String drawEcho, Long start, Long length) {
        return new DataTableResponse<>();
    }

    @Override
    public Response<Boolean> isGameLockedForPlayer(String domainName, String gameGuid, String playerGuid) {
        return Response.<Boolean>builder().status(Response.Status.NOT_IMPLEMENTED).build();
    }
}
