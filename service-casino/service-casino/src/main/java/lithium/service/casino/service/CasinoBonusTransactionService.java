package lithium.service.casino.service;

import lithium.client.changelog.Category;
import lithium.client.changelog.ChangeLogService;
import lithium.client.changelog.SubCategory;
import lithium.client.changelog.objects.ChangeLogFieldChange;
import lithium.service.Response;
import lithium.service.Response.Status;
import lithium.service.accounting.client.AccountingClient;
import lithium.service.accounting.objects.AdjustmentRequestComponent;
import lithium.service.accounting.objects.AdjustmentTransaction;
import lithium.service.casino.client.data.BalanceAdjustmentComponent;
import lithium.service.casino.client.data.EBalanceAdjustmentComponentType;
import lithium.service.casino.data.entities.PlayerBonus;
import lithium.service.casino.data.entities.PlayerBonusHistory;
import lithium.service.casino.data.entities.PlayerBonusPending;
import lithium.service.casino.data.repositories.PlayerBonusHistoryRepository;
import lithium.service.casino.data.repositories.PlayerBonusPendingRepository;
import lithium.service.casino.data.repositories.PlayerBonusRepository;
import lithium.service.casino.service.exception.AccountingTransactionFailureException;
import lithium.service.casino.service.util.AdjustmentRequestFactory;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.client.util.LabelManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionInterceptor;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class CasinoBonusTransactionService {
	@Autowired private CasinoBonusService casinoBonusService;
	@Autowired private ChangeLogService changeLogService;
	@Autowired private LithiumServiceClientFactory services;
	@Autowired private CasinoBonusFreespinService casinoBonusFreespinService;
	@Autowired private PlayerBonusPendingRepository playerBonusPendingRepository;
	@Autowired private AdjustmentRequestFactory adjustmentRequestFactory;
	@Autowired private PlayerBonusHistoryRepository playerBonusHistoryRepository;
	@Autowired private PlayerBonusRepository playerBonusRepository;
	@Autowired private CasinoMailSmsService casinoMailSmsService;
	
	/**
	 * Transaction for moving pending bonus balance to active bonus
	 * @param pb
	 * @param pbp
	 * @throws Exception
	 */
	@Transactional
//	@Retryable(maxAttempts = 3, exclude = { AccountingTransactionFailureException.class }, include = { Exception.class })
	public void movePendingBonusToActive(PlayerBonus pb, PlayerBonusPending pbp) throws Exception {
		// FIXME: 2019/09/10 Work on getting this compatable with instant bonuses etc. This is for the bonus rework process which is not in use currently.
		boolean instantBonus = false;
		PlayerBonusHistory pbh = casinoBonusService.savePlayerBonusHistory(
				pbp.getBonusRevision(),
				pbp.getPlayThroughRequiredCents(), 
				pbp.getBonusAmount(), 
				pbp.getBonusPercentage(),
				instantBonus,
				pbp.getCustomFreeMoneyAmountCents(),
				null, null, null, null, null);
		pb = casinoBonusService.savePlayerBonus(pbh, pbp.getPlayerGuid(), instantBonus);
		pbh = casinoBonusService.updatePlayerBonusHistory(pbh, pb, pbp.getBonusAmount());
		
		List<ChangeLogFieldChange> clfc = changeLogService.copy(pbh, new PlayerBonusHistory(), new String[] { "startedDate", "bonus" });
		changeLogService.registerChangesWithDomain("user.bonus", "registeredepositbonus", 0L, pbp.getPlayerGuid(), null, null, clfc, Category.BONUSES, SubCategory.BONUS_REGISTER, 0, pbp.getPlayerGuid().substring(0, pbp.getPlayerGuid().indexOf('/'))); //don't have the user event id, oh well
		
		LabelManager labelManager = new LabelManager();
		labelManager.addLabel(LabelManager.PLAYER_BONUS_HISTORY_ID, pb.getCurrent().getId().toString());
		labelManager.addLabel(LabelManager.BONUS_REVISION_ID, pb.getCurrent().getBonus().getId().toString());
		
		ArrayList<AdjustmentRequestComponent> adjustmentList = new ArrayList<>();
		adjustmentList.add(
			adjustmentRequestFactory.createTransferFromPendingToBonusRequestComponent(
				new BalanceAdjustmentComponent(
						EBalanceAdjustmentComponentType.CASINO_TRANSFER_FROM_PENDING_TO_BONUS, 
						pbp.getTriggerAmount() + pbp.getBonusAmount(), 
						null, null),
				labelManager,
				pbp.getBonusRevision().getDomain().getName(),
				pbp.getPlayerGuid()));
		Response<ArrayList<AdjustmentTransaction>> adjustmentResponse = getAccountingService().adjustMultiBatch(adjustmentList);
		if (adjustmentResponse.getStatus() != Status.OK) {
			TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
			log.error("Unable to perform accounting adjustment from pending bonus to active bonus.: Response: " + adjustmentResponse + " Request: " + adjustmentList);
			throw new AccountingTransactionFailureException();
		}
		//triggers to allow proper activation of bonus
		//FIXME: There are more bloody financial transactions in the below methods. They need to form part of a single call to accounting to keep things consistent.
		casinoBonusFreespinService.triggerFreeSpins(pbh, instantBonus);
		casinoBonusService.triggerFreeMoney(pbh, instantBonus); //FIXME: Should look into possibility of a duplicate causing a failure, that would not actually be a failure
		casinoBonusService.triggerAdditionalFreeMoney(pbh, instantBonus);
		casinoBonusService.triggerExternalBonusGame(pbh);
		playerBonusPendingRepository.deleteById(pbp.getId());
	}
	
	@Transactional
	public void cancelBonus(PlayerBonus playerBonus) throws Exception {
		PlayerBonus playerBonusCopy = new PlayerBonus();
		casinoBonusService.copyPlayerBonus(playerBonus, playerBonusCopy);
		casinoBonusFreespinService.cancelFreespins(playerBonus);
		PlayerBonusHistory pbh = playerBonus.getCurrent();
		pbh.setCancelled(true);
		playerBonusHistoryRepository.save(pbh);
		playerBonus.setCurrent(null);
		playerBonusRepository.save(playerBonus);
		
		try {
			casinoMailSmsService.sendBonusMail(CasinoMailSmsService.BONUS_STATE_CANCEL, playerBonusCopy.getCurrent(), null);
		} catch (Exception e) {
			log.error("Failed to send bonus cancel email " + playerBonus, e);
		}

		try {
			casinoMailSmsService.sendBonusSms(CasinoMailSmsService.BONUS_STATE_CANCEL, playerBonusCopy.getCurrent(), null);
		} catch (Exception e) {
			log.error("Failed to send bonus cancel sms " + playerBonus, e);
		}
	}
	
	/**
	 * Fetches the mapped feign client for the accounting service
	 * @return AccountingClient
	 */
	private AccountingClient getAccountingService() {
		AccountingClient cl = null;
		try {
			cl = services.target(AccountingClient.class, "service-accounting", true);
		} catch (LithiumServiceClientFactoryException e) {
			log.error("Problem getting accounting service", e);
		}
		return cl;
	}

	@Transactional
	public void completeBonus(PlayerBonus playerBonus) throws Exception {
		moveBalanceFromCasinoBonusToPlayer(playerBonus);
		PlayerBonusHistory pbh = playerBonus.getCurrent();
		pbh.setCompleted(true);
		playerBonusHistoryRepository.save(pbh);
		playerBonus.setCurrent(null);
		playerBonusRepository.save(playerBonus);
	}
	
	/**
	 * Perform a transfer of active bonus funds to player real balance.
	 * If the bonus add a max payout clause the excess funds are transfered to the excess account 
	 * @param playerBonus
	 * @throws Exception
	 */
	@Transactional
	public void moveBalanceFromCasinoBonusToPlayer(PlayerBonus playerBonus) throws Exception {
		
		Long casinoBonusBalance = casinoBonusService.getCasinoBonusBalance(playerBonus);
		Long maxPayout = playerBonus.getCurrent().getBonus().getMaxPayout();
		Long payout = casinoBonusBalance;
		Long excess = 0L;
		if ((maxPayout != null) && (maxPayout > 0)) {
			if (casinoBonusBalance > maxPayout) {
				payout = maxPayout;
				excess = casinoBonusBalance - maxPayout;
			}
		}
		
		LabelManager labelManager = new LabelManager();
		labelManager.addLabel(LabelManager.PLAYER_BONUS_HISTORY_ID, playerBonus.getCurrent().getId().toString());
		labelManager.addLabel(LabelManager.BONUS_REVISION_ID, playerBonus.getCurrent().getBonus().getId().toString());
		
		ArrayList<AdjustmentRequestComponent> adjustmentList = new ArrayList<>();

		//move balance from player casino bonus balance to player balance
		adjustmentList.add(
				adjustmentRequestFactory.createTransferFromBonusToRealRequestComponent(
					new BalanceAdjustmentComponent(
							EBalanceAdjustmentComponentType.CASINO_TRANSFER_FROM_BONUS_TO_REAL, 
							payout, 
							null, null),
					labelManager,
					playerBonus.getCurrent().getBonus().getDomain().getName(),
					playerBonus.getPlayerGuid()));
		if (excess > 0) {
			//max payout was specified, remaining funds to be transfered to 
			adjustmentList.add(
					adjustmentRequestFactory.createTransferFromBonusToExcessRequestComponent(
						new BalanceAdjustmentComponent(
								EBalanceAdjustmentComponentType.CASINO_TRANSFER_FROM_BONUS_TO_EXCESS, 
								payout, 
								null, null),
						labelManager,
						playerBonus.getCurrent().getBonus().getDomain().getName(),
						playerBonus.getPlayerGuid()));
		}
		
		Response<ArrayList<AdjustmentTransaction>> adjustmentResponse = getAccountingService().adjustMultiBatch(adjustmentList);
		if (adjustmentResponse.getStatus() != Status.OK) {
			TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
			log.error("Unable to perform accounting adjustment from active bonus to real and excess.: Response: " + adjustmentResponse + " Request: " + adjustmentList);
			throw new AccountingTransactionFailureException();
		}
	}
}
