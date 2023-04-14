package lithium.service.cashier.processor.btc.upay;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import lithium.service.cashier.client.internal.DoProcessorRequest;
import lithium.service.cashier.client.internal.DoProcessorResponse;
import lithium.service.cashier.client.internal.DoProcessorResponseStatus;
import lithium.service.cashier.method.btc.DoProcessorBTCAdapter;
import lithium.service.cashier.processor.DoProcessorContext;
import lithium.service.cashier.processor.upay.btc.data.LoadFromBitcoinRequest;
import lithium.service.cashier.processor.upay.btc.data.LoadFromBitcoinResponse;

@Service
@Slf4j
public class DoProcessor extends DoProcessorBTCAdapter {
	
	@Override
	protected DoProcessorResponseStatus depositStage1(DoProcessorRequest request, DoProcessorResponse response, DoProcessorContext context, RestTemplate rest) throws Exception {
		LoadFromBitcoinRequest upayRequest = LoadFromBitcoinRequest.builder()
			.accountId(request.getProperty("receiver_account"))
			.amount(request.stageInputData(1, "amount"))
			.currency(request.getUser().getCurrency())
			.orderId(request.getTransactionId().toString())
			.key(request.getProperty("apikey"))
			.build().sign(request.getProperty("apisecret"));
		log.debug(upayRequest.toString());
		
		LoadFromBitcoinResponse upayResponse = postForObject(request, response, context, rest,
			request.getProperty("apiurl") + "/api/merchant/v/1.0/function/load_from_bitcoin", 
			upayRequest,
			LoadFromBitcoinResponse.class
		);
		
		if (!upayResponse.getStatus().equals("success")) {
			response.setMessage(upayResponse.getMsg());
			buildRawResponseLog(response, upayResponse);
			return DoProcessorResponseStatus.INPUTERROR;
		}
		
		response.stageOutputData(1).put("account_info", upayResponse.getBitCoinAddress());
		response.stageOutputData(1).put("address", upayResponse.getBitCoinAddress());
		response.stageOutputData(1).put("bitcoins", upayResponse.getBitCoinAmount());
		
		buildRawResponseLog(response, upayResponse);
		return DoProcessorResponseStatus.NEXTSTAGE;

	}

	@Override
	protected DoProcessorResponseStatus depositStage2(DoProcessorRequest request, DoProcessorResponse response, DoProcessorContext context, RestTemplate rest) throws Exception {
		return DoProcessorResponseStatus.NOOP;
	}
}
