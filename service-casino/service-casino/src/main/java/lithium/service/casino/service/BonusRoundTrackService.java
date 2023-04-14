package lithium.service.casino.service;

import java.util.List;

import lithium.service.casino.data.entities.Game;
import lithium.service.casino.data.repositories.GameRepository;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lithium.service.casino.client.objects.request.BalanceAdjustmentRequest;
import lithium.service.casino.client.objects.request.BetRequest;
import lithium.service.casino.data.entities.BonusRoundTrack;
import lithium.service.casino.data.entities.PlayerBonusHistory;
import lithium.service.casino.data.repositories.BonusRoundTrackRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class BonusRoundTrackService {
	
	@Autowired private BonusRoundTrackRepository bonusRoundTrackRepository;
	@Autowired private GameRepository gameRepository;

	/**
	 * Retrieve a specific bonus round object from the database
	 * @param betRequest
	 * @return The bonus round tracking info or null if it does not exist
	 */
	@Deprecated
	public BonusRoundTrack findBonusRound(final BetRequest betRequest) {
		if (!isRoundInfoValid(betRequest)) {
			return null;
		}
		
		BonusRoundTrack bonusRoundTrack = bonusRoundTrackRepository.
				findByPlayerBonusHistoryPlayerBonusPlayerGuidAndGameGuidAndRoundId(betRequest.getUserGuid(), betRequest.getGameGuid(), betRequest.getRoundId());
		
		return bonusRoundTrack;
	}
	
	/**
	 * Retrieve a specific bonus round object from the database
	 * @param betRequest
	 * @return The bonus round tracking info or null if it does not exist
	 */
	public BonusRoundTrack findBonusRound(final BalanceAdjustmentRequest betRequest) {
		if (!isRoundInfoValid(betRequest)) {
			return null;
		}
		
		BonusRoundTrack bonusRoundTrack = bonusRoundTrackRepository.
				findByPlayerBonusHistoryPlayerBonusPlayerGuidAndGameGuidAndRoundId(betRequest.getUserGuid(), betRequest.getGameGuid(), betRequest.getRoundId());
		
		return bonusRoundTrack;
	}
	
	/**
	 * Retrieve a specific bonus round object from the database or create one based on the bet request
	 * If an object is found and the round completion flag of the bet request is different from the DB value
	 * it is updated and saved
	 * @param betRequest
	 * @param playerBonusHistory
	 * @return The bonus round tracking info or null if it does not exist
	 */
	@Deprecated
	public BonusRoundTrack createOrUpdateBonusRound(final BetRequest betRequest, final PlayerBonusHistory playerBonusHistory) {
		if (!isRoundInfoValid(betRequest)) {
			return null;
		}
		
		BonusRoundTrack bonusRoundTrack = findBonusRound(betRequest);
		
		if (bonusRoundTrack == null) {
			bonusRoundTrack = BonusRoundTrack.builder()
								.playerBonusHistory(playerBonusHistory)
								.playerBonus(playerBonusHistory.getPlayerBonus())
								.createdDate(DateTime.now().toDate())
								.game(gameRepository.findOrCreateByGuid(betRequest.getGameGuid(), () -> new Game()))
								.roundId(betRequest.getRoundId())
								.completed(false)
								.build();
			bonusRoundTrack = bonusRoundTrackRepository.save(bonusRoundTrack);
			log.debug("The round has been created for betRequest: " + betRequest + " bonusRoundTrack: " + bonusRoundTrack);
		}
		
		if (betRequest.getRoundFinished() != null && 
				betRequest.getRoundFinished() == Boolean.TRUE && 
				bonusRoundTrack.isCompleted() == false) {
			
			bonusRoundTrack.setCompletedDate(DateTime.now().toDate());
			bonusRoundTrack.setCompleted(true);
			bonusRoundTrack = bonusRoundTrackRepository.save(bonusRoundTrack);
			log.debug("The round has finished for betRequest: " + betRequest + " bonusRoundTrack: " + bonusRoundTrack);
			
			return bonusRoundTrack;
		}
		
		if (betRequest.getRoundFinished() != null && 
				betRequest.getRoundFinished() == Boolean.FALSE && 
				bonusRoundTrack.isCompleted() == true) {
			
			bonusRoundTrack.setCompletedDate(null);
			bonusRoundTrack.setCompleted(false);
			bonusRoundTrack = bonusRoundTrackRepository.save(bonusRoundTrack);
			log.debug("The round has finished for betRequest: " + betRequest + " bonusRoundTrack: " + bonusRoundTrack);
			
			return bonusRoundTrack;
		}
		
		//We assume a null in the round finished flag means it is not implemented, so we complete it.
		if (betRequest.getRoundFinished() == null) {
			bonusRoundTrack.setCompletedDate(DateTime.now().toDate());
			bonusRoundTrack = bonusRoundTrackRepository.save(bonusRoundTrack);
			log.debug("The round has finished because we have a null in completion info betRequest: " + betRequest + " bonusRoundTrack: " + bonusRoundTrack);
		}
		
		return bonusRoundTrack;
	}
	
	/**
	 * Retrieve a specific bonus round object from the database or create one based on the bet request
	 * If an object is found and the round completion flag of the bet request is different from the DB value
	 * it is updated and saved
	 * @param adjustmentRequest
	 * @param playerBonusHistory
	 * @return The bonus round tracking info or null if it does not exist
	 */
	public BonusRoundTrack createOrUpdateBonusRound(final BalanceAdjustmentRequest adjustmentRequest, final PlayerBonusHistory playerBonusHistory) {
		if (playerBonusHistory == null || !isRoundInfoValid(adjustmentRequest)) {
			return null;
		}
		
		BonusRoundTrack bonusRoundTrack = findBonusRound(adjustmentRequest);
		
		if (bonusRoundTrack == null) {
			bonusRoundTrack = BonusRoundTrack.builder()
								.playerBonusHistory(playerBonusHistory)
								.playerBonus(playerBonusHistory.getPlayerBonus())
								.createdDate(DateTime.now().toDate())
								.game(gameRepository.findOrCreateByGuid(adjustmentRequest.getGameGuid(), () -> new Game()))
								.roundId(adjustmentRequest.getRoundId())
								.completed(false)
								.build();
			bonusRoundTrack = bonusRoundTrackRepository.save(bonusRoundTrack);
			log.debug("The round has been created for betRequest: " + adjustmentRequest + " bonusRoundTrack: " + bonusRoundTrack);
		}
		
		if (adjustmentRequest.getRoundFinished() != null && 
				adjustmentRequest.getRoundFinished() == Boolean.TRUE && 
				bonusRoundTrack.isCompleted() == false) {
			
			bonusRoundTrack.setCompletedDate(DateTime.now().toDate());
			bonusRoundTrack.setCompleted(true);
			bonusRoundTrack = bonusRoundTrackRepository.save(bonusRoundTrack);
			log.debug("The round has finished for betRequest: " + adjustmentRequest + " bonusRoundTrack: " + bonusRoundTrack);
			
			return bonusRoundTrack;
		}
		
		if (adjustmentRequest.getRoundFinished() != null && 
				adjustmentRequest.getRoundFinished() == Boolean.FALSE && 
				bonusRoundTrack.isCompleted() == true) {
			
			bonusRoundTrack.setCompletedDate(null);
			bonusRoundTrack.setCompleted(false);
			bonusRoundTrack = bonusRoundTrackRepository.save(bonusRoundTrack);
			log.debug("The round has finished for betRequest: " + adjustmentRequest + " bonusRoundTrack: " + bonusRoundTrack);
			
			return bonusRoundTrack;
		}
		
		//We assume a null in the round finished flag means it is not implemented, so we complete it.
		if (adjustmentRequest.getRoundFinished() == null) {
			bonusRoundTrack.setCompletedDate(DateTime.now().toDate());
			bonusRoundTrack = bonusRoundTrackRepository.save(bonusRoundTrack);
			log.debug("The round has finished because we have a null in completion info betRequest: " + adjustmentRequest + " bonusRoundTrack: " + bonusRoundTrack);
		}
		
		return bonusRoundTrack;
	}
	
	/**
	 * Finds the list of unfinished rounds of a specific player bonus
	 * @param playerBonusHistory
	 * @return a list of bonus round tracking objects
	 */
	public List<BonusRoundTrack> getUnfinishedRoundsOnBonus(final PlayerBonusHistory playerBonusHistory) {
		return bonusRoundTrackRepository.findByPlayerBonusHistoryAndCompletedFalse(playerBonusHistory);
	}
	
	/**
	 *  Flag to indicate whether there are outstanding rounds on a specific player bonus
	 * @param playerBonusHistory
	 * @return flag for unfinished rounds
	 */
	public boolean isUnfinishedRoundsOnBonus(final PlayerBonusHistory playerBonusHistory) {
		if (playerBonusHistory == null) return false;
		Long unfinishedRounds = bonusRoundTrackRepository.countByPlayerBonusHistoryAndCompletedFalse(playerBonusHistory);
		log.debug("Number of unfinished rounds on bonus: " + unfinishedRounds + " for bonus: " + playerBonusHistory);
		if (unfinishedRounds != null && unfinishedRounds > 0L) {
			return true;
		}
		
		return false;
	}
	
	/**
	 * Determines whether the bet request has sufficient data for round tracking 
	 * @param betRequest
	 * @return true if valid or false if invalid
	 */
	@Deprecated
	private boolean isRoundInfoValid(final BetRequest betRequest) {
		if (betRequest == null) {
			log.debug("Round info evaluation failed on bet request. The bet request is null");
			return false;
		}
		
		if (betRequest.getUserGuid() == null || betRequest.getUserGuid().isEmpty()) {
			log.debug("Round info evaluation failed on user guid: " + betRequest);
			return false;
		}
		
		if (betRequest.getGameGuid() == null || betRequest.getGameGuid().isEmpty()) {
			log.debug("Round info evaluation failed on game guid: " + betRequest);
			return false;
		}
		
		if (betRequest.getRoundId() == null || betRequest.getRoundId().isEmpty()) {
			log.debug("Round info evaluation failed on round guid: " + betRequest);
			return false;
		}
		
		if (betRequest.getRoundFinished() == null) {
			log.debug("Round info evaluation failed on round finished: " + betRequest);
			return false;
		}
		
		return true;
	}
	
	/**
	 * Determines whether the bet request has sufficient data for round tracking 
	 * @param betRequest
	 * @return true if valid or false if invalid
	 */
	private boolean isRoundInfoValid(final BalanceAdjustmentRequest betRequest) {
		if (betRequest == null) {
			log.debug("Round info evaluation failed on bet request. The bet request is null");
			return false;
		}
		
		if (betRequest.getUserGuid() == null || betRequest.getUserGuid().isEmpty()) {
			log.debug("Round info evaluation failed on user guid: " + betRequest);
			return false;
		}
		
		if (betRequest.getGameGuid() == null || betRequest.getGameGuid().isEmpty()) {
			log.debug("Round info evaluation failed on game guid: " + betRequest);
			return false;
		}
		
		if (betRequest.getRoundId() == null || betRequest.getRoundId().isEmpty()) {
			log.debug("Round info evaluation failed on round guid: " + betRequest);
			return false;
		}
		
		if (betRequest.getRoundFinished() == null) {
			log.debug("Round info evaluation failed on round finished: " + betRequest);
			return false;
		}
		
		return true;
	}
}
