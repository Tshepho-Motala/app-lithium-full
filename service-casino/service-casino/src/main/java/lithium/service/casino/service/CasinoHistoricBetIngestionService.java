package lithium.service.casino.service;

import lithium.exceptions.Status500InternalServerErrorException;
import lithium.service.casino.data.entities.Domain;
import lithium.service.casino.data.entities.User;
import lithium.service.casino.data.enums.BetRequestKindEnum;
import lithium.service.casino.data.enums.BetResultRequestKindEnum;
import lithium.service.casino.data.repositories.DomainRepository;
import lithium.service.casino.data.repositories.UserRepository;
import lithium.service.libraryvbmigration.data.dto.BetsMigrationDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CasinoHistoricBetIngestionService {

  private final BetPersistService betPersistService;
  private final BetResultPersistService betResultPersistService;

  private final GameCacheService gameCacheService;
  private final DomainRepository domainRepository;
  private final UserRepository userRepository;

  /** We can not use a @Retryable here will just end up just queueing unnecessary resource hogs.
   *  We should rather try to fail fast if more than one request for the same user
   * comes in. This call will block on the DB based on player guid. LSPLAT-9892 */
  @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
  public void startBetIngestion(BetsMigrationDetails details)
      throws Status500InternalServerErrorException {

      Domain domain = domainRepository.findOrCreateByName(details.getDomainName(), Domain::new);
      User player = userRepository.findOrCreateByGuid(details.getPlayerGuid(), () -> User.builder().domain(domain).build());

      BetRequestKindEnum betRequestKindEnum = details.getAmount() > 0 ? BetRequestKindEnum.BET : BetRequestKindEnum.FREE_BET;
      BetResultRequestKindEnum betResultRequestKindEnum = details.getReturns() > 0 ?
          (betRequestKindEnum == BetRequestKindEnum.BET ? BetResultRequestKindEnum.WIN: BetResultRequestKindEnum.WIN) :
          (betRequestKindEnum == BetRequestKindEnum.BET ? BetResultRequestKindEnum.LOSS: BetResultRequestKindEnum.FREE_LOSS);

      String gameName = gameCacheService.findGameNameByGuidAndDomain(details.getProviderGuid() + "_" + details.getProviderGameId(), details.getDomainName());

      if(gameName.isEmpty() || gameName.equals("Freespin Bonus")){
        // We are checking here if the game actually exists on the Games Service, and failing fast LSPLAT-9892
        throw new Status500InternalServerErrorException("Game not found on Game Service");
      }

      String gameId = details.getDomainName().concat("/").concat(details.getProviderGuid() + "_" + details.getProviderGameId());
      String providerGuid = details.getDomainName().concat("/").concat(details.getProviderGuid());

      betPersistService.persist(
          details.getCurrencyCode(),
          gameId,
          betRequestKindEnum,
          providerGuid,
          details.getBetId(),
          details.getBetId(),
          false,
          null,
          details.getAmount(),
          details.getPlacementDateTime(),
          player.getGuid(),
          details.getDomainName(),
          null,
          null,
          true,
          null
          );

      betResultPersistService.persist(
          player.getGuid(),
          gameId,
          details.getDomainName(),
          providerGuid,
          details.getBetId(),
          details.getCurrencyCode(),
          betResultRequestKindEnum,
          false,
          null,
          details.getBetId(),
          true,
          details.getReturns(),
          details.getSettlementDateTime(),
          null
      );
  }
}
