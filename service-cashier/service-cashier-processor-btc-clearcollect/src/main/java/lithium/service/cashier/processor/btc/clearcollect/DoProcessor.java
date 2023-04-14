package lithium.service.cashier.processor.btc.clearcollect;

import java.math.BigDecimal;
import java.util.Date;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import lithium.service.cashier.client.internal.DoProcessorRequest;
import lithium.service.cashier.client.internal.DoProcessorResponse;
import lithium.service.cashier.client.internal.DoProcessorResponseStatus;
import lithium.service.cashier.method.btc.DoProcessorBTCAdapter;
import lithium.service.cashier.processor.DoProcessorContext;
import lithium.service.cashier.processor.btc.clearcollect.data.DepositRequest;
import lithium.service.cashier.processor.btc.clearcollect.data.DepositResponse;
import lithium.service.cashier.processor.btc.clearcollect.data.GetDepositRequest;
import lithium.service.cashier.processor.btc.clearcollect.data.GetDepositResponse;
import lithium.service.cashier.processor.btc.clearcollect.data.GetWithdrawalRequest;
import lithium.service.cashier.processor.btc.clearcollect.data.GetWithdrawalResponse;
import lithium.service.cashier.processor.btc.clearcollect.data.RequestOrResponse;
import lithium.service.cashier.processor.btc.clearcollect.data.WithdrawRequest;
import lithium.service.cashier.processor.btc.clearcollect.data.WithdrawResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class DoProcessor extends DoProcessorBTCAdapter {

	@Override
	protected DoProcessorResponseStatus depositStage1(DoProcessorRequest request, DoProcessorResponse response, DoProcessorContext context, RestTemplate rest) throws Exception {
		DepositRequest deposit = new DepositRequest();
		deposit.setNonce(Long.toString(new Date().getTime()));
		deposit.setAmountUsdCents(new Long(Math.round(Double.parseDouble(request.stageInputData(1, "amount")) * 100)).toString());
		deposit.setClienttracking(request.getUser().getGuid());
		deposit.setSender(request.getUser().getFullName());
		
		RequestOrResponse<DepositRequest> clearCollectRequest = new RequestOrResponse<>(
			request.getProperty("apisecret"),
			request.getProperty("apikey"),
			deposit
		);
		
		DepositResponse clearCollectResponse = postForObject(
			request, response, context, rest, 
			request.getProperty("apiurl")+"/deposit/new/json",
			clearCollectRequest,
			clearCollectRequest.createHttpEntity(),
			DepositResponse.class
		);
		long satoshis = Long.parseLong(clearCollectResponse.getDeposit().getRequestAmountBtcSatoshis());
		BigDecimal bitcoins = BigDecimal.valueOf(satoshis * 0.00000001);
		
		response.stageOutputData(1).put("address", clearCollectResponse.getDeposit().getAddress());
		response.stageOutputData(1).put("account_info", clearCollectResponse.getDeposit().getAddress());
		response.stageOutputData(1).put("bitcoins", bitcoins.toString());
		response.stageOutputData(1).put("transaction_id", clearCollectResponse.getDeposit().getId());
		response.setProcessorReference(clearCollectResponse.getDeposit().getId());
		
		buildRawResponseLog(response, clearCollectResponse);
		return DoProcessorResponseStatus.NEXTSTAGE;
	}
	
	@Override
	protected DoProcessorResponseStatus depositStage2(DoProcessorRequest request, DoProcessorResponse response, DoProcessorContext context, RestTemplate rest) throws Exception {
		GetDepositRequest clearCollectRequest = GetDepositRequest.builder()
			.id(request.stageOutputData(1, "transaction_id"))
			.build();
		clearCollectRequest.setNonce(Long.toString(new Date().getTime()));
		
		RequestOrResponse<GetDepositRequest> clearCollectRequestWrapper = new RequestOrResponse<>(
			request.getProperty("apisecret"),
			request.getProperty("apikey"),
			clearCollectRequest
		);
		
		GetDepositResponse clearCollectResponse = postForObject(
			request, response, context, rest,
			request.getProperty("apiurl")+"/deposit/get/json",
			clearCollectRequestWrapper,
			clearCollectRequestWrapper.createHttpEntity(),
			GetDepositResponse.class
		);
		
		//"EXPIRATIONDATETIME":"November, 02 2017 05:14:46"
		try {
//			DateTimeFormatter f = DateTimeFormat.forPattern("MMMMM, dd yyyy HH:mm:ss");
//			LocalDateTime dateTime = f.parseLocalDateTime(clearCollectResponse.getDeposit().getExpirationDateTime());
			
//			LocalDateTime now = LocalDateTime.now();
			String minutesToExpire = clearCollectResponse.getDeposit().getMinutesToExpire();
			if (clearCollectResponse.getSuccess().equals("true") && clearCollectResponse.getDeposit().getStatus().equals("P")) {
//				if (minutesToExpire != null) {
//					now = now.plusMinutes(Integer.parseInt(minutesToExpire));
//					response.setExpiryDate(now.toDateTime());
//				} else {
//					log.warn("No Expiry Time received.");
				log.debug("Ignoring clearcollect sent expiry time : "+minutesToExpire);
				response.setExpiryDate(null);
//				}
			} else if (clearCollectResponse.getSuccess().equals("true") && clearCollectResponse.getDeposit().getStatus().equals("C")) {
				response.setRemoveTtl(true);
			}
		} catch (Exception e) {
			log.error("Could not parse expiry date "+e.getMessage(), e);
			response.setExpiryDate(null);
			response.setRemoveTtl(true);
		}

		try {
			if (clearCollectResponse.getSuccess().equals("true") && clearCollectResponse.getDeposit().getStatus().equals("C")) {
				response.setAmountCentsReceived(Integer.parseInt(clearCollectResponse.getDeposit().getCreditAmountUsdInt()));
			}
		} catch (Exception e) {
			response.addRawResponseLog("Invalid creditamountusdint in response: " + e.toString());
			log.error("Invalid creditamountusdint in response", e);
		}
		
		if (clearCollectResponse.getSuccess().equals("true") && clearCollectResponse.getDeposit().getStatus().equals("C")) {
			try {
				int confirmationsRequired = Integer.parseInt(request.getProperty("btcconfirmations"));
				Integer confirmations = new Integer(clearCollectResponse.getDeposit().getConfirmations());
				if (confirmations >= confirmationsRequired) {
					buildRawResponseLog(response, clearCollectResponse);
					return DoProcessorResponseStatus.SUCCESS;
				} else {
					response.addRawResponseLog("Not enough confirmations: " + confirmations);
					response.setMessage("The transaction is currently awaiting confirmation.");
				}
			} catch (NumberFormatException nfe) {
				log.error("Invalid confirmation number: ", nfe);
				response.addRawResponseLog("Not enough confirmations: " + nfe);
			}
		}
		buildRawResponseLog(response, clearCollectResponse);
		
		return DoProcessorResponseStatus.NOOP;
	}

	@Override
	protected DoProcessorResponseStatus withdrawStage1(DoProcessorRequest request, DoProcessorResponse response, DoProcessorContext context, RestTemplate rest) throws Exception {
		WithdrawRequest clearCollectRequest = WithdrawRequest.builder()
			.amountUsdInt(new Long(Math.round(Double.parseDouble(request.stageInputData(1, "amount")) * 100)).toString())
			.authorize("true")
			.beneficiary(request.getUser().getFullName())
			.clientTracking(request.getTransactionId().toString())
			.outputAddress(request.stageInputData(1, "address"))
			.build();
		clearCollectRequest.setNonce(Long.toString(new Date().getTime()));

		RequestOrResponse<WithdrawRequest> clearCollectRequestWrapper = new RequestOrResponse<WithdrawRequest>(
			request.getProperty("apisecret"),
			request.getProperty("apikey"),
			clearCollectRequest
		);
		
		WithdrawResponse clearCollectResponse = postForObject(
			request, response, context, rest,
			request.getProperty("apiurl") + "/withdraw/new/json",
			clearCollectRequestWrapper.createHttpEntity(),
			WithdrawResponse.class
		);
		
		if (!clearCollectResponse.getSuccess().equals("true")) {
			buildRawResponseLog(response, clearCollectResponse);
			return DoProcessorResponseStatus.DECLINED;
		}
		response.stageOutputData(1).put("transaction_id", clearCollectResponse.getData().getId());
		buildRawResponseLog(response, clearCollectResponse);
		return DoProcessorResponseStatus.NEXTSTAGE;
	}
	
	@Override
	protected DoProcessorResponseStatus withdrawStage2(DoProcessorRequest request, DoProcessorResponse response, DoProcessorContext context, RestTemplate rest) throws Exception {
		GetWithdrawalRequest clearCollectRequest = GetWithdrawalRequest.builder()
			.id(request.stageOutputData(1, "transaction_id"))
			.build();
		clearCollectRequest.setNonce(Long.toString(new Date().getTime()));
		
		RequestOrResponse<GetWithdrawalRequest> clearCollectRequestWrapper = new RequestOrResponse<GetWithdrawalRequest>(
			request.getProperty("apisecret"),
			request.getProperty("apikey"),
			clearCollectRequest
		);
		
		GetWithdrawalResponse clearCollectResponse = postForObject(
			request, response, context, rest,
			request.getProperty("apiurl") + "/withdraw/get/json",
			clearCollectRequestWrapper.createHttpEntity(),
			GetWithdrawalResponse.class
		);
		buildRawResponseLog(response, clearCollectResponse);
		
		if (clearCollectResponse.getSuccess().equals("true") && clearCollectResponse.getData().getStatus().equals("C")) {
			return DoProcessorResponseStatus.SUCCESS;
		}
		return DoProcessorResponseStatus.NOOP;
	}
}
