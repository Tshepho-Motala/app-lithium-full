package lithium.service.games.services;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


import lithium.service.games.client.enums.UserGameStatus;
import lithium.service.games.client.objects.SimpleGameUserStatus;

import lithium.client.changelog.Category;
import lithium.client.changelog.ChangeLogService;
import lithium.client.changelog.SubCategory;
import lithium.client.changelog.objects.ChangeLogFieldChange;

import lithium.service.games.exceptions.Status406NoGamesEnabledException;
import lithium.service.user.client.exceptions.UserClientServiceFactoryException;
import lithium.service.user.client.service.UserApiInternalClientService;
import lithium.tokens.LithiumTokenUtil;
import lithium.tokens.LithiumTokenUtilService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import lithium.service.games.data.entities.Game;
import lithium.service.games.data.entities.GameUserStatus;
import lithium.service.games.data.entities.User;
import lithium.service.games.data.repositories.GameUserStatusRepository;
import lombok.extern.slf4j.Slf4j;

import static java.util.Optional.ofNullable;

@Slf4j
@Service
public class GameUserStatusService {
    @Autowired
    private UserService userService;

    @Autowired
    private GameService gameService;

    @Autowired
    private GameUserLockStatusService gameUserLockStatusService;

    @Autowired
    private CashierClientService cashierClientService;

    @Autowired
    private GameUserStatusRepository gameUserStatusRepository;

    @Autowired
    private ChangeLogService changeLogService;


    @Autowired
    LithiumTokenUtilService tokenService;

    @Autowired
    UserApiInternalClientService userApiInternalClientService;

    public GameUserStatus find(Game game, String userGuid) {
        User user = userService.findOrCreate(userGuid);
        return findOrCreate(game, user);
    }

    public GameUserStatus findOrNull(String gameGuid, String userGuid) {
        User user = userService.findOrCreate(userGuid);
        Game game = gameService.findByGameAndDomainName(gameGuid, user.domainName());
        return findOrNull(game, user);
    }

    public GameUserStatus findOrNull(Game game, User user) {
        return gameUserStatusRepository.findByGameAndUser(game, user);
    }

    public GameUserStatus findOrNull(String gameGuid, User user) {
        return gameUserStatusRepository.findByGameGuidAndUser(gameGuid, user);
    }

    public GameUserStatus findOrCreate(Game game, User user) {
        GameUserStatus gus = gameUserStatusRepository.findByGameAndUser(game, user);
        if (gus == null) {
            gus = gameUserStatusRepository.save(
                    GameUserStatus.builder()
                            .enabled(game.isEnabled())
                            .game(game)
                            .locked(true)
                            .user(user)
                            .build()
            );
        }
        return gus;
    }

    public List<GameUserStatus> findByUser(String playerGuid) {
        User user = userService.findOrCreate(playerGuid);
        return gameUserStatusRepository.findByUser(user);
    }

    public GameUserStatus save(GameUserStatus gameUserStatus) {
        gameUserStatus = gameUserStatusRepository.save(gameUserStatus);
        gameUserLockStatusService.stream(gameUserStatus.playerGuid());
        return gameUserStatus;
    }

    public Page<GameUserStatus> findAllByGame(Game game, PageRequest pageRequest) {
        log.debug("findAllByGame Game : " + game);
        return gameUserStatusRepository.findAllByGame(game, pageRequest);
    }

    public GameUserStatus toggleLocked(Game game, User user) {
        user = userService.findOrCreate(user.guid());
        log.debug("toggle User : " + user + " : Game : " + game);
        GameUserStatus gus = findOrCreate(game, user);
        gus.setLocked(!gus.getLocked());

        return save(gus);
    }

    public void toggleDelete(Game game, String userGuid) {
        User user = userService.findOrCreate(userGuid);
//		log.debug("toggle User : "+user+" : Game : "+game);
        toggleLocked(game, user);
        GameUserStatus gus = findOrNull(game, user);
        if (gus != null) gameUserStatusRepository.delete(gus);
//		gameUserLockStatusService.stream(user.guid());
    }

    public List<GameUserStatus> findUnlockedFreeGamesForUser(String userGuid, LithiumTokenUtil tokenUtil) throws Status406NoGamesEnabledException {
        User user = userService.findOrCreate(userGuid);
        List<GameUserStatus> unlockedForUser = findByUser(userGuid);
        Boolean hasGameStatuses = !unlockedForUser.isEmpty();
        if (hasGameStatuses) {
            return unlockedForUser.stream().filter(gus -> !gus.getLocked()).collect(Collectors.toList());
        }

        Boolean firstDepositExist = Objects.nonNull(cashierClientService.getFirstDeposit(userGuid));
        if (!firstDepositExist)
            return new ArrayList<>();
        try {
            // https://jira.livescore.com/browse/PLAT-3539 Unlock required for existing users who have made a deposit
            unlockAllFreeGames(userGuid, tokenUtil);
        } catch (Status406NoGamesEnabledException | UserClientServiceFactoryException e) {
            // We are ignoring this exception. Expected behaviour for lobby load
        }
        return gameUserStatusRepository.findByUserAndLockedFalse(user);
    }

    public GameUserStatus unlock(String gameGuid, User user) {
        user = userService.findOrCreate(user.guid());
        Game game = gameService.findByGameAndDomainName(gameGuid, user.domainName());
        return unlock(game, user);
    }

    public GameUserStatus unlock(Game game, User user) {
        GameUserStatus gameUserStatus = findOrCreate(game, user);
        gameUserStatus.setLocked(false);
        return gameUserStatusRepository.save(gameUserStatus);
    }

    public void unlockAllFreeGames(String userGuid, LithiumTokenUtil tokenUtil) throws Status406NoGamesEnabledException, UserClientServiceFactoryException {
        User user = userService.findOrCreate(userGuid);
        lithium.service.user.client.objects.User externalUser = userApiInternalClientService.getUserByGuid(userGuid);
        List<Game> freeGameList = gameService.findDomainFreeGames(user.domainName());
        String authorGuid = ofNullable(tokenUtil).map(LithiumTokenUtil::guid).orElse(lithium.service.user.client.objects.User.SYSTEM_GUID);
        if(freeGameList.size() == 0){
            throw new Status406NoGamesEnabledException("Unlock failed. No free games available on domain: " + user.domainName());
        }
        freeGameList.forEach(freeGame -> unlock(freeGame, user));
        try{
            ChangeLogFieldChange changeLogFieldChange = ChangeLogFieldChange.builder()
                    .field("freeGamesTag")
                    .fromValue(String.valueOf(false))
                    .toValue(String.valueOf(true))
                    .build();
            changeLogService.registerChangesForNotesWithFullNameAndDomain("user", "edit", externalUser.getId(), authorGuid, tokenUtil, "Free Game tag was updated","", Arrays.asList(changeLogFieldChange),Category.ACCOUNT, SubCategory.EDIT_DETAILS,0,user.domainName());
        } catch (Exception e) {
            log.error(ofNullable(tokenUtil).map(LithiumTokenUtil::userLegalName).orElse(lithium.service.user.client.objects.User.SYSTEM_FULL_NAME) + " has failed to apply the FreeGamesTag to the account [" + user.guid() + "] for the following reason: \n"+ e.getMessage(), e);
        }
    }

    public void lockAllFreeGames(String userGuid, LithiumTokenUtil tokenUtil) throws UserClientServiceFactoryException {
        User user = userService.findOrCreate(userGuid);
        lithium.service.user.client.objects.User externalUser = userApiInternalClientService.getUserByGuid(userGuid);
        List<Game> freeGameList = gameService.findDomainFreeGames(user.domainName());
        freeGameList.forEach(freeGame -> lock(freeGame, user));
        try{
            ChangeLogFieldChange changeLogFieldChange = ChangeLogFieldChange.builder()
                    .field("freeGamesTag")
                    .fromValue(String.valueOf(true))
                    .toValue(String.valueOf(false))
                    .build();
            changeLogService.registerChangesForNotesWithFullNameAndDomain("user", "edit",  externalUser.getId(), user.getGuid(),tokenUtil, "Free Game tag was updated","", Arrays.asList(changeLogFieldChange),Category.ACCOUNT, SubCategory.EDIT_DETAILS,0,user.domainName());

        } catch (Exception e){
            String authorGuid = tokenUtil.userLegalName();
            log.error(authorGuid + " has failed to apply the FreeGamesTag to the account [" + user.guid() + "] for the following reason: \n"+ e.getMessage(), e);
        }
    }

    public GameUserStatus lock(Game game, User user) {
        GameUserStatus gameUserStatus = findOrCreate(game, user);
        gameUserStatus.setLocked(true);
        return gameUserStatusRepository.save(gameUserStatus);
    }

    public List<SimpleGameUserStatus> unlockMultipleGamesForUserOnDomain(String userGuid, List<String> gameGuids, String domainName) {
        User user = userService.findOrCreate(userGuid);
        List<SimpleGameUserStatus> simpleGameUserStatusList = new ArrayList<>();

        for (String gameGuid: gameGuids) {
            Game game = gameService.findByGameAndDomainName(gameGuid, domainName);

            if (game == null) {
                log.error(MessageFormat.format("Could not unlock game with guid: {0} for user:{1} because the game does not exist for domain {2}",
                        gameGuid, userGuid, domainName));
                continue;
            }

            GameUserStatus gameUserStatus = unlock(game, user);

            simpleGameUserStatusList.add(SimpleGameUserStatus.builder()
                            .status(UserGameStatus.UNLOCKED)
                            .gameName(game.getName())
                            .gameGuid(gameGuid)
                            .userGuid(userGuid)
                    .build());
        }

        if (!simpleGameUserStatusList.isEmpty()) {

            String gameList =  simpleGameUserStatusList.stream().map(SimpleGameUserStatus::getGameName)
                    .collect(Collectors.joining(","));

            log.info(MessageFormat.format("Unlocked games ({0}) for user:{1}", gameList, userGuid));
        }

        return simpleGameUserStatusList;
    }

}
