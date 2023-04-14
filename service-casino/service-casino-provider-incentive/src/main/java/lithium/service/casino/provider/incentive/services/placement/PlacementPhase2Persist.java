package lithium.service.casino.provider.incentive.services.placement;

import lithium.metrics.SW;
import lithium.service.casino.provider.incentive.api.exceptions.Status409DuplicateSubmissionException;
import lithium.service.casino.provider.incentive.api.schema.placement.request.PlacementRequest;
import lithium.service.casino.provider.incentive.api.schema.placement.request.PlacementRequestBet;
import lithium.service.casino.provider.incentive.api.schema.placement.request.PlacementRequestEvent;
import lithium.service.casino.provider.incentive.services.EventService;
import lithium.service.casino.provider.incentive.storage.entities.Bet;
import lithium.service.casino.provider.incentive.storage.entities.BetSelection;
import lithium.service.casino.provider.incentive.storage.entities.Competition;
import lithium.service.casino.provider.incentive.storage.entities.Currency;
import lithium.service.casino.provider.incentive.storage.entities.Domain;
import lithium.service.casino.provider.incentive.storage.entities.Event;
import lithium.service.casino.provider.incentive.storage.entities.IncentiveUser;
import lithium.service.casino.provider.incentive.storage.entities.Market;
import lithium.service.casino.provider.incentive.storage.entities.Placement;
import lithium.service.casino.provider.incentive.storage.entities.Selection;
import lithium.service.casino.provider.incentive.storage.entities.SelectionType;
import lithium.service.casino.provider.incentive.storage.entities.Sport;
import lithium.service.casino.provider.incentive.storage.entities.User;
import lithium.service.casino.provider.incentive.storage.repositories.BetRepository;
import lithium.service.casino.provider.incentive.storage.repositories.BetSelectionRepository;
import lithium.service.casino.provider.incentive.storage.repositories.CompetitionRepository;
import lithium.service.casino.provider.incentive.storage.repositories.CurrencyRepository;
import lithium.service.casino.provider.incentive.storage.repositories.DomainRepository;
import lithium.service.casino.provider.incentive.storage.repositories.IncentiveUserRepository;
import lithium.service.casino.provider.incentive.storage.repositories.MarketRepository;
import lithium.service.casino.provider.incentive.storage.repositories.PlacementRepository;
import lithium.service.casino.provider.incentive.storage.repositories.SelectionRepository;
import lithium.service.casino.provider.incentive.storage.repositories.SelectionTypeRepository;
import lithium.service.casino.provider.incentive.storage.repositories.SportRepository;
import lithium.service.casino.provider.incentive.storage.repositories.UserRepository;
import lithium.tokens.LithiumTokenUtil;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@Slf4j
@RequiredArgsConstructor
public class PlacementPhase2Persist {

    final @Setter
    UserRepository userRepository;

    final @Setter
    IncentiveUserRepository incentiveUserRepository;

    final @Setter
    CurrencyRepository currencyRepository;

    final @Setter
    MarketRepository marketRepository;

    final @Setter
    EventService eventService;

    final @Setter
    PlacementRepository placementRepository;

    final @Setter
    BetRepository betRepository;

    final @Setter
    DomainRepository domainRepository;

    final @Setter
    BetSelectionRepository betSelectionRepository;

    final @Setter
    SportRepository sportRepository;

    final @Setter
    CompetitionRepository competitionRepository;

    final @Setter
    SelectionTypeRepository selectionTypeRepository;

    final @Setter
    SelectionRepository selectionRepository;

    public Placement persist(PlacementRequest request, LithiumTokenUtil tokenUtil) throws Status409DuplicateSubmissionException {

        log.debug("placement.persist " + request);

        SW.start("placement.persist.findorcreates");

        User user = userRepository.findOrCreateByGuid(tokenUtil.guid(), () -> new User());
        IncentiveUser userI = incentiveUserRepository.findOrCreateByGuid(request.getUserId(), () -> new IncentiveUser());
        Currency currency = currencyRepository.findOrCreateByCode(request.getCurrencyCode(), () -> new Currency());
        Domain domain = domainRepository.findOrCreateByName(tokenUtil.domainName(), () -> new Domain());

        SW.stop();
        SW.start("placement.persist.placement");

        Placement placement = new Placement();
        placement.setCurrency(currency);
        placement.setIncentiveUser(userI);
        placement.setUser(user);
        placement.setExtraData(request.getExtraData());
        placement.setDomain(domain);
        placement.setSessionId(tokenUtil.sessionId());
        placement = placementRepository.save(placement);

        SW.stop();

        for (PlacementRequestBet betRequest : request.getBets()) {

            SW.start("placement.persist.bet." + betRequest.getBetTransactionId());

            Bet bet = new Bet();
            bet.setTransactionTimestamp(new Date(betRequest.getTransactionTimestamp()));
            bet.setMaxPotentialWin(betRequest.getMaxPotentialWin());
            bet.setTotalOdds(betRequest.getTotalOdds());
            bet.setTotalStake(betRequest.getTotalStake());
            bet.setBetTransactionId(betRequest.getBetTransactionId());
            bet.setPlacement(placement);
            bet.setVirtualCoinId(betRequest.getVirtualCoinId());
            placement.getBets().add(bet);
            try {
                bet = betRepository.save(bet);
            } catch (DataIntegrityViolationException e) {
                throw new Status409DuplicateSubmissionException(
                        "Bet with transaction ID " + bet.getBetTransactionId() + " is already registered");
            }
            for (PlacementRequestEvent eventRequest : betRequest.getEvents()) {
                Market market = marketRepository.findOrCreateByCode(eventRequest.getMarketCode(),
                        () -> Market.builder().name(eventRequest.getMarketName()).build());

                Sport sport = (eventRequest.getSportCode() == null) ? null:
                        sportRepository.findOrCreateByCode(eventRequest.getSportCode(),
                        () -> Sport.builder().name(eventRequest.getSportName()).build());

                // Incentive Games have no names for the leagues, only codes. Perhaps we choose to add names somehow later.
                Competition competition = (eventRequest.getCompetitionCode() == null) ? null:
                        competitionRepository.findOrCreateByCode(eventRequest.getCompetitionCode(),
                        () -> Competition.builder().build());

                Selection selection = selectionRepository.findOrCreateByGuid(eventRequest.getSelectionId(),
                        () -> Selection.builder().build());

                SelectionType selectionType = selectionTypeRepository.findOrCreateByCode(eventRequest.getSelectionCode(),
                        () -> SelectionType.builder().name(eventRequest.getSelectionName()).build());

                Event event = eventService.findOrCreate(eventRequest.getEventName(), eventRequest.getEventStartTime());

                BetSelection betSelection = new BetSelection();
                betSelection.setBet(bet);
                betSelection.setSport(sport);
                betSelection.setCompetition(competition);
                betSelection.setEvent(event);
                betSelection.setMarket(market);
                betSelection.setPrice(eventRequest.getPrice());
                betSelection.setSelection(selection);
                betSelection.setSelectionType(selectionType);
                betSelectionRepository.save(betSelection);

            }

            log.info("Bet " + betRequest);

            SW.stop();
        }

        return placement;
    }

}
