package lithium.service.casino.search.data.repositories.casino;

import lithium.service.casino.data.entities.Bet;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository("casino.BetRepository")
public interface BetRepository extends PagingAndSortingRepository<Bet, Long> {
  @Query("SELECT COALESCE(SUM(bet.amount), 0) "
      + " FROM Bet bet "
      + " INNER JOIN bet.betRound betRound "
      + " INNER JOIN betRound.provider provider "
      + " INNER JOIN bet.kind kind "
      + " WHERE betRound.id = :betRoundId "
      + " AND provider.id = :providerId "
      + " AND kind.code = 'BET' ")
  public double getBetAmount(@Param("betRoundId") Long betRoundId, @Param("providerId") Long providerId);

  @Query("SELECT COALESCE(SUM(bet.amount), 0) "
      + " FROM Bet bet "
      + " INNER JOIN bet.betRound betRound "
      + " INNER JOIN betRound.provider provider "
      + " INNER JOIN bet.kind kind "
      + " WHERE betRound.id = :betRoundId "
      + " AND provider.id = :providerId "
      + " AND kind.code = 'BET_REVERSAL' ")
  public double getBetReversalAmount(@Param("betRoundId") Long betRoundId, @Param("providerId") Long providerId);
}
