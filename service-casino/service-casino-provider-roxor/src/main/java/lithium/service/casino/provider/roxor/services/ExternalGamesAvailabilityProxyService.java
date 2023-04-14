package lithium.service.casino.provider.roxor.services;

import lithium.modules.ModuleInfo;
import lithium.service.casino.exceptions.Status512ProviderNotConfiguredException;
import lithium.service.casino.provider.roxor.config.ProviderConfig;
import lithium.service.casino.provider.roxor.config.ProviderConfigProperties;
import lithium.service.casino.provider.roxor.config.ProviderConfigService;
import lithium.service.casino.provider.roxor.storage.entities.Domain;
import lithium.service.casino.provider.roxor.storage.entities.Game;
import lithium.service.casino.provider.roxor.storage.entities.GamePlay;
import lithium.service.casino.provider.roxor.storage.entities.GamesAvailability;
import lithium.service.casino.provider.roxor.storage.entities.MfgDetails;
import lithium.service.casino.provider.roxor.storage.entities.PlayerDetails;
import lithium.service.casino.provider.roxor.storage.entities.Prize;
import lithium.service.casino.provider.roxor.storage.entities.Summary;
import lithium.service.casino.provider.roxor.storage.entities.User;
import lithium.service.casino.provider.roxor.storage.entities.Wins;
import lithium.service.casino.provider.roxor.storage.repositories.DomainRepository;
import lithium.service.casino.provider.roxor.storage.repositories.GamePlayRepository;
import lithium.service.casino.provider.roxor.storage.repositories.GameRepository;
import lithium.service.casino.provider.roxor.storage.repositories.GamesAvailabilityRepository;
import lithium.service.casino.provider.roxor.storage.repositories.UserRepository;
import lithium.service.domain.client.CachingDomainClientService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
public class ExternalGamesAvailabilityProxyService {
    @Autowired
    GameRepository gameRepository;
    @Autowired
    private GamePlayRepository gamePlayRepository;
    @Autowired
    private GamesAvailabilityRepository gamesAvailabilityRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    DailyFreeGameExternalService dailyFreeGameExternalService;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    ProviderConfigService providerConfigService;
    @Autowired
    ModuleInfo moduleInfo;
    @Autowired
    CachingDomainClientService cachingDomainClientService;
    @Autowired
    DomainRepository domainRepository;

    @Transactional
    public void doExternalGamesAvailabilityChecks(String guid, String userGuid) throws Exception {
        SimpleDateFormat formatDate = new SimpleDateFormat("yyyy/MM/dd");
        Date currentDate = new Date();
        String todayDate = formatDate.format(currentDate);
        Date currentDateFinal = formatDate.parse(todayDate);
        List<GamesAvailability> gamesAvailabilities = gamesAvailabilityRepository.findByCreationDateAndUser_Guid(currentDateFinal, userGuid);
        if (gamesAvailabilities.isEmpty()) {
            createOrUpdateGamesAvailability(gamesAvailabilities, guid, userGuid);
        }
    }

    public List<lithium.service.casino.provider.roxor.data.response.freegames.GamesAvailability> getPlayerGamesAvailability(String userGuid, String domainName, String apiToken) throws Exception {
        SimpleDateFormat formatDate = new SimpleDateFormat("yyyy/MM/dd");
        Date currentDate = new Date();
        String todayDate = formatDate.format(currentDate);
        Date currentDateFinal = formatDate.parse(todayDate);
        List<GamesAvailability> gamesAvailabilities = gamesAvailabilityRepository.findByCreationDateAndUser_Guid(currentDateFinal, userGuid);
        List<lithium.service.casino.provider.roxor.data.response.freegames.GamesAvailability> playerGamesAvailibilityList = modelMapper.map(gamesAvailabilities, new TypeToken<List<lithium.service.casino.provider.roxor.data.response.freegames.GamesAvailability>>(){}.getType());
        if (playerGamesAvailibilityList.isEmpty()) {
            ProviderConfig pc = null;
            try {
                pc = providerConfigService.getConfig(moduleInfo.getModuleName(), domainName);
                validateConfigs(pc);
            } catch (Status512ProviderNotConfiguredException e) {
                log.info("Provider Roxor not configured for domain : " + domainName);
                return null;
            } catch (IllegalArgumentException e) {
                log.info("ExternalGamesAvailabilityProxyService.doExternalGamesAvailabilityChecks: Provider is missing games availability endpoint properties. " + e.getMessage());
                return null;
            }

            Domain domain = domainRepository.findOrCreateByName(domainName,
                    () -> new Domain());
            User user = userRepository.findOrCreateByGuid(userGuid, () ->
                    User.builder()
                            .guid(userGuid)
                            .apiToken(apiToken)
                            .domain(domain)
                            .build());
            playerGamesAvailibilityList = dailyFreeGameExternalService.getFreeGames(domain.getName(), user.getApiToken(), pc.getFreeGamesUrl(), pc.getWebsite());
            for (lithium.service.casino.provider.roxor.data.response.freegames.GamesAvailability gamesAvailability : playerGamesAvailibilityList) {
                gamesAvailability.setCreationDate(currentDateFinal);
            }
        }
        return playerGamesAvailibilityList;
    }

    private void validateConfigs(ProviderConfig pc) {
        List<String> missingProperties = new ArrayList<>();
        if (pc.getFreeGamesUrl() == null || pc.getFreeGamesUrl().trim().isEmpty()) {
            missingProperties.add(ProviderConfigProperties.FREE_GAMES_URL.getValue());
        }
        if (pc.getWebsite() == null || pc.getWebsite().trim().isEmpty()) {
            missingProperties.add(ProviderConfigProperties.WEBSITE.getValue());
        }

        if (!missingProperties.isEmpty()) {
            if (!missingProperties.isEmpty()) {
                String missingPropertiesStr = String.join(", ", missingProperties);
                throw new IllegalArgumentException("One or more required configuration properties not set."
                        + " ["+missingPropertiesStr+"]");
            }
        }
    }

    public void createOrUpdateGamesAvailability(List<GamesAvailability> gamesAvailabilities, String guid, String userGuid) throws Exception {
        Game game = gameRepository.findByGuid(guid);
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date date = cal.getTime();
        long dateLong = date.getTime();
        List<GamePlay> playerGamePlayList = gamePlayRepository.findByUserGuidAndCreatedDateGreaterThanEqualOrderByIdAsc(userGuid, dateLong);
        if (playerGamePlayList != null && playerGamePlayList.size() > 0) {
            GamePlay gamePlay = playerGamePlayList.get(0);
            ProviderConfig pc = null;
            String domainName = gamePlay.getUser().getDomain().getName();
            try {
                pc = providerConfigService.getConfig(moduleInfo.getModuleName(), domainName);
                validateConfigs(pc);
            } catch (Status512ProviderNotConfiguredException e) {
                log.info("Provider Roxor not configured for domain : " + domainName);
                return;
            } catch (IllegalArgumentException e) {
                log.info("ExternalGamesAvailabilityProxyService.createOrUpdateGamesAvailability: Provider is missing games availability endpoint properties. " + e.getMessage());
                return;
            }

            String userApiToken = gamePlay.getUser().getApiToken();
            List<lithium.service.casino.provider.roxor.data.response.freegames.GamesAvailability> gamesAvailabilityList = dailyFreeGameExternalService.getFreeGames(domainName, userApiToken, pc.getFreeGamesUrl(), pc.getWebsite());
            if (Objects.isNull(gamesAvailabilityList))
                return;
            for (int i = 0; i < gamesAvailabilityList.size(); i++) {
                Summary summary = null;
                PlayerDetails playerDetails = new PlayerDetails();
                if (gamesAvailabilityList.get(i).getSummary() != null) {
                    ArrayList<Wins> winsList = new ArrayList<>();
                    if (gamesAvailabilityList.get(i).getSummary().getPlayerDetails() != null) {
                        for (int j = 0; j < gamesAvailabilityList.get(i).getSummary().getPlayerDetails().getWins().size(); j++) {
                            Wins wins = new Wins();
                            Prize prizeMap = modelMapper.map(gamesAvailabilityList.get(i).getSummary().getPlayerDetails().getWins().get(j).getPrize(), new TypeToken<Prize>() {}.getType());
                            wins.setPrize(prizeMap);
                            wins.setTimestamp(gamesAvailabilityList.get(i).getSummary().getPlayerDetails().getWins().get(j).getTimestamp());
                            wins.setScreenName(gamesAvailabilityList.get(i).getSummary().getPlayerDetails().getWins().get(j).getScreenName());
                            winsList.add(wins);
                        }
                        playerDetails.setWins(winsList);
                        playerDetails.setCurrentDay(gamesAvailabilityList.get(i).getSummary().getPlayerDetails().getCurrentDay());
                        ArrayList<String> daysPlayedList = new ArrayList<>();
                        for (int m = 0; m < gamesAvailabilityList.get(i).getSummary().getPlayerDetails().getDaysPlayed().size(); m++) {
                            daysPlayedList.add(gamesAvailabilityList.get(i).getSummary().getPlayerDetails().getDaysPlayed().get(m));
                        }
                        playerDetails.setDaysPlayed(daysPlayedList);
                        playerDetails.setDfgPicksRemaining(Optional.ofNullable(gamesAvailabilityList.get(i).getSummary().getPlayerDetails().getDfgPicksRemaining()).orElse(""));

                        MfgDetails mfgDetails = new MfgDetails();
                        mfgDetails.setMfgStatus(gamesAvailabilityList.get(i).getSummary().getPlayerDetails().getMfgDetails().getMfgStatus());
                        mfgDetails.setMfgEarnedPicks(gamesAvailabilityList.get(i).getSummary().getPlayerDetails().getMfgDetails().getMfgEarnedPicks());
                        playerDetails.setMfgDetails(mfgDetails);
                        Summary summaryMap = modelMapper.map(gamesAvailabilityList.get(i).getSummary(), new TypeToken<Summary>() {}.getType());
                        summary = summaryMap;
                        summary.setPlayerDetails(playerDetails);
                    }
                }
                Date creationDate = new Date(gamePlay.getCreatedDate());
                User user = userRepository.findById(gamePlay.getUser().getId()).orElse(null);
                GamesAvailability gamesAvailability = (gamesAvailabilities.isEmpty()) ? new GamesAvailability() : gamesAvailabilities.get(0);
                gamesAvailability.setGameKey(gamesAvailabilityList.get(i).getGameKey());
                gamesAvailability.setStatus(gamesAvailabilityList.get(i).getStatus());
                gamesAvailability.setSummary(summary);
                gamesAvailability.setUser(user);
                gamesAvailability.setGame(game);
                gamesAvailability.setCreationDate(creationDate);
                gamesAvailabilityRepository.save(gamesAvailability);
            }

        }
    }
}
