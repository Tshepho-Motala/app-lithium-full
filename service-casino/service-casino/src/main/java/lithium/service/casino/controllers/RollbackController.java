package lithium.service.casino.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lithium.service.Response;
import lithium.service.Response.Status;
import lithium.service.casino.CasinoTranType;
import lithium.service.casino.client.objects.request.RollbackTranRequest;
import lithium.service.casino.client.objects.response.RollbackTranResponse;
import lithium.service.casino.data.objects.TranProcessResponse;
import lithium.service.casino.service.CasinoService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class RollbackController {
	@Autowired
	private CasinoService casinoService;
	
	@RequestMapping("/casino/rollbackTran")
	public RollbackTranResponse rollbackTran(@RequestBody RollbackTranRequest rollbackBetRequest) throws Exception {
		//TODO: Check for active bonus ?
		//FIXME: Yes, bonus checking is certainly needed and should be added when we get time. It causes balance inconsistencies if the bonus has ended and rollback needs to happen
		log.error("Need to implement bonus checking.");
		TranProcessResponse rollbackResponse= null;
		Long tranId = -1L;
		boolean duplicate = false;
		
		String currency = (rollbackBetRequest.getCurrencyCode() != null && !rollbackBetRequest.getCurrencyCode().isEmpty())
						? rollbackBetRequest.getCurrencyCode()
						: casinoService.getCurrency(rollbackBetRequest.getDomainName());
		rollbackResponse = casinoService.processReversal(
				rollbackBetRequest.getDomainName(), rollbackBetRequest.getUserGuid(), rollbackBetRequest.getTransactionId(),
				"CASINO_BET_" + casinoService.accountCodeFromProviderGuid(rollbackBetRequest.getProviderGuid()), 
				CasinoTranType.CASINO_BET.toString(),
				CasinoTranType.CASINO_BET_ROLLBACK.toString(),
				currency
		);
		log.debug("Rollback response bet: " + rollbackResponse);
		if (rollbackResponse != null && rollbackResponse.getTranId() != null) {
			tranId = rollbackResponse.getTranId();
			duplicate |= rollbackResponse.isDuplicate();
		}

		rollbackResponse = casinoService.processReversal(
				rollbackBetRequest.getDomainName(), rollbackBetRequest.getUserGuid(), rollbackBetRequest.getTransactionId(),
				"CASINO_WIN_" + casinoService.accountCodeFromProviderGuid(rollbackBetRequest.getProviderGuid()), 
				CasinoTranType.CASINO_WIN.toString(),
				CasinoTranType.CASINO_WIN_ROLLBACK.toString(),
				currency
		);
		log.debug("Rollback response win: " + rollbackResponse);
		if (rollbackResponse != null && rollbackResponse.getTranId() != null) {
			tranId = rollbackResponse.getTranId();
			duplicate |= rollbackResponse.isDuplicate();
		}
		
		TranProcessResponse tmpRollbackResponse = casinoService.processReversal(
				rollbackBetRequest.getDomainName(), rollbackBetRequest.getUserGuid(), rollbackBetRequest.getTransactionId(),
				"CASINO_NEGATIVEBET_" + casinoService.accountCodeFromProviderGuid(rollbackBetRequest.getProviderGuid()), 
				CasinoTranType.CASINO_NEGATIVE_BET.toString(),
				CasinoTranType.CASINO_NEGATIVE_BET_ROLLBACK.toString(),
				currency
		);
		
		log.debug("Rollback response negative bet: " + rollbackResponse);
		// We do not check and use negativeBetTranId, see betController. <-- wtf, think this is incorrect
		if ((tranId == null || tranId == -1L) && (tmpRollbackResponse != null && tmpRollbackResponse.getTranId() != null)) {
			tranId = tmpRollbackResponse.getTranId();
			rollbackResponse = tmpRollbackResponse;
			duplicate |= rollbackResponse.isDuplicate();
		}

		
		RollbackTranResponse btr = RollbackTranResponse.builder()
				.balanceCents(casinoService.getCustomerBalanceWithError(currency,rollbackBetRequest.getDomainName(), rollbackBetRequest.getUserGuid())) //call balance service
				.tranId(tranId+"") // call accounting service
				.build();
		
		if(tranId == null || tranId <= 0) {
			btr.setCode("-1");
			btr.setResult("Problem performing rollback transaction " + rollbackBetRequest);
		} else if (duplicate) {
			btr.setCode("DUPLICATE");
		}
		
		log.debug("Rollback transaction response: " + btr + " more: " + btr.getCode()+ " duplicate: " + duplicate);
		return btr;
	}
	
	@RequestMapping("/casino/rollbackTest")
	public Response<RollbackTranResponse> rollbackTranTest(@RequestParam("userGuid") String userGuid,
			@RequestParam("transactionId") String transactionId,
			@RequestParam("domainName") String domainName,
			@RequestParam("providerGuid") String providerGuid) throws Exception {
		RollbackTranRequest req = new RollbackTranRequest();
		req.setDomainName(domainName);
		req.setProviderGuid(providerGuid);
		req.setTransactionId(transactionId);
		req.setUserGuid(userGuid);
		return Response.<RollbackTranResponse>builder().data(rollbackTran(req)).status(Status.OK).build();
	}
}
