package lithium.service.casino.provider.incentive.services.pickanyentry;

import lithium.service.casino.provider.incentive.api.schema.pickany.entry.PickAnyEntryRequest;
import lithium.service.casino.provider.incentive.api.schema.pickany.entry.PickAnyEntryRequestPick;
import lithium.service.casino.provider.incentive.context.PickAnyEntryContext;
import lithium.service.casino.provider.incentive.services.EventService;
import lithium.service.casino.provider.incentive.storage.entities.Competition;
import lithium.service.casino.provider.incentive.storage.entities.Currency;
import lithium.service.casino.provider.incentive.storage.entities.Domain;
import lithium.service.casino.provider.incentive.storage.entities.Event;
import lithium.service.casino.provider.incentive.storage.entities.IncentiveUser;
import lithium.service.casino.provider.incentive.storage.entities.PickAnyEntry;
import lithium.service.casino.provider.incentive.storage.entities.PickAnyEntryPick;
import lithium.service.casino.provider.incentive.storage.entities.PickAnyGame;
import lithium.service.casino.provider.incentive.storage.entities.User;
import lithium.service.casino.provider.incentive.storage.repositories.CompetitionRepository;
import lithium.service.casino.provider.incentive.storage.repositories.DomainRepository;
import lithium.service.casino.provider.incentive.storage.repositories.EventRepository;
import lithium.service.casino.provider.incentive.storage.repositories.PickAnyEntryPickRepository;
import lithium.service.casino.provider.incentive.storage.repositories.PickAnyEntryRepository;
import lithium.service.casino.provider.incentive.storage.repositories.PickAnyGameRepository;
import lithium.service.casino.provider.incentive.storage.repositories.UserRepository;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@Slf4j
public class PickAnyEntryPhase2Persist {

    @Autowired @Setter
    PickAnyEntryRepository entryRepository;

    @Autowired @Setter
    PickAnyEntryPickRepository entryPickRepository;

    @Autowired @Setter
    PickAnyGameRepository gameRepository;

    @Autowired @Setter
    CompetitionRepository competitionRepository;

    @Autowired @Setter
    UserRepository userRepository;

    @Autowired @Setter
    DomainRepository domainRepository;

    @Autowired @Setter
    EventService eventService;

    public void persist(PickAnyEntryContext context) {
        PickAnyEntryRequest request = context.getRequest();

        PickAnyGame game = gameRepository.findOrCreateByCode(request.getGameCode(),
                () -> new PickAnyGame());
        Competition competition = competitionRepository.findOrCreateByCode(request.getCompetitionCode(),
                () -> new Competition());
        User user = userRepository.findOrCreateByGuid(context.getPlayerGuid(),
                () -> new User());
        Domain domain = domainRepository.findOrCreateByName(context.getDomainName(),
                () -> new Domain());

        PickAnyEntry entry = new PickAnyEntry();
        context.setEntry(entry);

        entry.setDomain(domain);
        entry.setUser(user);
        entry.setCompetition(competition);
        entry.setGame(game);
        entry.setEntryTransactionId(request.getEntryTransactionId());
        entry.setEntryTimestamp(new Date(request.getEntryTimestamp()));
        entry.setPredictorId(request.getPredictorId());
        entry.setSessionId(context.getSessionId());
        entry = entryRepository.save(entry);
        context.setEntry(entry);

        for (PickAnyEntryRequestPick requestPick : request.getPicks()) {
            Event event = eventService.findOrCreate(requestPick.getEventName(), requestPick.getEventStartTime());
            PickAnyEntryPick pick = new PickAnyEntryPick();
            pick.setEntry(entry);
            pick.setIncentiveEventId(requestPick.getEventId());
            pick.setEvent(event);
            pick.setAwayScore(requestPick.getAwayScore());
            pick.setHomeScore(requestPick.getHomeScore());
            entryPickRepository.save(pick);
        }

    }

}
