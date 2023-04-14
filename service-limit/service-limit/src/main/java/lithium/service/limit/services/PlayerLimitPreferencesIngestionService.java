package lithium.service.limit.services;

import lithium.metrics.SW;
import lithium.metrics.TimeThisMethod;
import lithium.service.limit.client.objects.PlayerLimitPreferenceMigrationDetails;
import lithium.service.limit.data.entities.User;
import lithium.service.limit.data.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Service
@Slf4j
@RequiredArgsConstructor
public class PlayerLimitPreferencesIngestionService {



    private final PlayerLimitService playerLimitService;
    private final UserRepository userRepository;

    @TimeThisMethod
    public void initiatePlayerLimitPreferences(PlayerLimitPreferenceMigrationDetails details)
        throws Exception {
         SW.start("Player Limit Preferences");

            User user = userRepository.findOrCreateByGuid(details.getPlayerGuid(), User :: new);
            playerLimitService.savePlayerLimitMigration(
                    user.getGuid(),
                    Long.valueOf( user.getGuid().split("/")[1]),
                    details.getGranularity(),
                    details.getAmountCents(),
                    details.getLimitType(),
                    details.getDomainName()
            );
            SW.stop();
    }
}

