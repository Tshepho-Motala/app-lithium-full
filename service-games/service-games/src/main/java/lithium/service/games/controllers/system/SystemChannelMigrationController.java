package lithium.service.games.controllers.system;

import lithium.service.games.config.ServiceGamesConfigurationProperties;
import lithium.service.games.client.objects.ChannelMigrationJob;
import lithium.service.games.data.entities.ChannelMigration;
import lithium.service.games.data.repositories.ChannelMigrationRepository;
import lithium.service.games.services.GameService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/system/run-channel-migration-job")
@RestController
@Slf4j
public class SystemChannelMigrationController {

    @Autowired ServiceGamesConfigurationProperties properties;
    @Autowired GameService gameService;

    @Autowired ChannelMigrationRepository channelMigrationRepository;

    @PostMapping
    public void doChannelMigration(@RequestBody ChannelMigrationJob channelMigrationJob) {
        ChannelMigration migrationRunner = null;

        migrationRunner = channelMigrationRepository.findById(1L)
                .orElse(channelMigrationRepository.save(ChannelMigration.builder()
                        .id(1L)
                        .running(false)
                        .build()));

        if (migrationRunner.isRunning()) {
            log.error("Channel migration task still running.");
            return;
        }

        updateChannelMigrationResetRunningState(migrationRunner, true);
        log.info("Channel Migration Job starting, checking if job is allowed to run | enabled:" + properties.getChannelMigrationJob().isEnabled());

        if (channelMigrationJob != null && properties.getChannelMigrationJob().isEnabled()) {
            log.info("Starting Channel Migration Job with properties: " + properties.getChannelMigrationJob());
            gameService.migrateOsLabelToChannelLabels(channelMigrationJob);
        }

        updateChannelMigrationResetRunningState(migrationRunner, false);
        log.info("Channel migration job runner completed, releasing lock for next run");
    }

    private void updateChannelMigrationResetRunningState(ChannelMigration channelMigration, boolean running) {
        if (channelMigration != null) {
            channelMigration.setRunning(running);
            channelMigration = channelMigrationRepository.save(channelMigration);
            log.info("{} Channel migration job reset", (running) ? "Starting" : "Completed");
        }
    }
}


