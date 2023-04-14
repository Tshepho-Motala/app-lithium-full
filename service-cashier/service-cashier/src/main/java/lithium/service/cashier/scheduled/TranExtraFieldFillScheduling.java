package lithium.service.cashier.scheduled;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import lithium.leader.LeaderCandidate;
import lithium.service.cashier.data.entities.Transaction;
import lithium.service.cashier.data.entities.TransactionData;
import lithium.service.cashier.services.TransactionService;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Service
public class TranExtraFieldFillScheduling {
	@Autowired
	private TransactionService transactionService;
	@Autowired
	private LeaderCandidate leaderCandidate;

	public void doTransactionFill() {
		if (!leaderCandidate.iAmTheLeader()) {
			log.debug("I am not the leader.");
			return;
		}
		List<Transaction> transactions = transactionService.findTransWithEmptyFields();
		if ((transactions == null) || (transactions.size() == 0)) {
			log.debug("No Transactions found to check.");
		} else {
			for (Transaction t:transactions) {
				log.debug("Found : "+t);
				List<TransactionData> dataList = transactionService.data(t);
				for (TransactionData data:dataList) {
					if ("bonusCode".equalsIgnoreCase(data.getField()) && !data.isOutput()) t.setBonusCode(data.getValue());
					//CC
					else if ("ccnumber".equalsIgnoreCase(data.getField()) && !data.isOutput()) t.setAccountInfo(data.getValue());
					//BTC
					else if ("address".equalsIgnoreCase(data.getField())) t.setAccountInfo(data.getValue());
					//UPAY
					else if ("sender".equalsIgnoreCase(data.getField()) && !data.isOutput()) t.setAccountInfo(data.getValue());
					else if ("receiver_account".equalsIgnoreCase(data.getField()) && !data.isOutput()) t.setAccountInfo(data.getValue());
					//MG/WU
					else if ("control_number".equalsIgnoreCase(data.getField()) && !data.isOutput()) t.setAccountInfo(data.getValue());
					else if ("processorUserId".equalsIgnoreCase(data.getField())) t.setAccountInfo(data.getValue());
					else t.setAccountInfo("");
				}
				transactionService.update(t); //, userService.findOrCreate("system"), "Processed BonusCode/AccountInfo");
			}
		}
	}
}
