package lithium.service.limit.services;

import lithium.service.accounting.client.transactiontyperegister.TransactionTypeRegisterService;
import lithium.service.limit.client.AccountType;
import lithium.service.limit.client.LimitTranType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * The functionality in this service deals with player balance limits.
 */
@Slf4j
@Service
public class PlayerBalanceLimitService {
	public static final String TRAN_ID_LABEL = "transaction_id";

	@Autowired
	private TransactionTypeRegisterService transactionTypeService;

	/**
	 * This method creates the transaction types needed to provide functionality for balance limits.
	 */
	public void startup() {
		log.debug("start transaction types needed to provides functionality for balance limits.");
		{
			Long ttid = transactionTypeService.create(LimitTranType.TRANSFER_TO_BALANCE_LIMIT_ESCROW.toString()).getData().getId();
			transactionTypeService.addAccount(ttid, AccountType.PLAYER_BALANCE.toString(), true, true);
			transactionTypeService.addUniqueLabel(ttid, TRAN_ID_LABEL, false, AccountType.PLAYER_BALANCE.toString());
		}
		{
			Long ttid = transactionTypeService.create(LimitTranType.TRANSFER_FROM_BALANCE_LIMIT_ESCROW.toString()).getData().getId();
			transactionTypeService.addAccount(ttid, AccountType.PLAYER_BALANCE.toString(), true, true);
			transactionTypeService.addUniqueLabel(ttid, TRAN_ID_LABEL, false, AccountType.PLAYER_BALANCE.toString());
		}
		transactionTypeService.register();
		log.debug("end transaction types needed to provides functionality for balance limits.");
	}
}
