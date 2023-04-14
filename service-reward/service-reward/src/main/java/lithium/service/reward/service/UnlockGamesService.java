package lithium.service.reward.service;

import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.games.client.GamesClient;
import lithium.service.games.client.service.GamesInternalClientService;
import lithium.service.reward.client.dto.RewardRevisionTypeGame;
import lithium.service.reward.client.dto.User;
import lithium.service.reward.provider.client.dto.ProcessRewardResponse;
import lithium.service.reward.provider.client.dto.ProcessRewardStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UnlockGamesService {

    private final LithiumServiceClientFactory lithiumServiceClientFactory;

    public ProcessRewardResponse unlockGames(User user, List<RewardRevisionTypeGame> rewardRevisionTypeGames) {

        ProcessRewardResponse processRewardResponse = ProcessRewardResponse.builder()
                .valueGiven(0L)
                .valueInCents(0L)
                .amountAffected(0L)
                .valueUsed(0L)
                .build();

        try {

            for(RewardRevisionTypeGame rewardRevisionTypeGame: rewardRevisionTypeGames) {
                gamesClient().unlock(rewardRevisionTypeGame.getGuid(), lithium.service.games.client.objects.User.builder()
                        .guid(user.getGuid())
                        .build());
            }

            processRewardResponse.setStatus(ProcessRewardStatus.SUCCESS);
        }

        catch (Exception e) {
            processRewardResponse.setStatus(ProcessRewardStatus.FAILED);
            String gameListString = rewardRevisionTypeGames.stream().map(RewardRevisionTypeGame::getGuid).collect(Collectors.joining(","));
            log.error(MessageFormat.format("Failed to unlockGames [{0}]", gameListString), e);
        }

        return processRewardResponse;
    }
    public GamesClient gamesClient() {
        GamesClient gamesClient = null;

        try {
            gamesClient = lithiumServiceClientFactory.target(GamesClient.class, true);
        } catch (LithiumServiceClientFactoryException e) {
            log.error("Failed to get gamesclient");
        }

        return gamesClient;
    }
}
