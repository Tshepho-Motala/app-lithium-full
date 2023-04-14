package lithium.service.cashier.processor.upay.upay;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import lithium.service.cashier.client.frontend.DoErrorException;
import lithium.service.cashier.client.internal.DoProcessorRequest;
import lithium.service.cashier.client.internal.DoProcessorResponse;
import lithium.service.cashier.client.internal.DoProcessorResponseStatus;
import lithium.service.cashier.method.upay.DoProcessorUpayAdapter;
import lithium.service.cashier.processor.DoProcessorContext;
import lithium.service.cashier.processor.upay.upay.data.FinishTransferRequest;
import lithium.service.cashier.processor.upay.upay.data.FinishTransferResponse;
import lithium.service.cashier.processor.upay.upay.data.GetTransactionStatusRequest;
import lithium.service.cashier.processor.upay.upay.data.GetTransactionStatusResponse;
import lithium.service.cashier.processor.upay.upay.data.InitializeTransferRequest;
import lithium.service.cashier.processor.upay.upay.data.InitializeTransferResponse;
import lithium.service.cashier.processor.upay.upay.data.TransferAccountToAccountRequest;
import lithium.service.cashier.processor.upay.upay.data.TransferAccountToAccountResponse;
import lithium.service.cashier.validator.FieldValidator;

@Service
@Slf4j
public class DoProcessor extends DoProcessorUpayAdapter {
	public static final String AMOUNT_FIELD = "amount";
	
	@Override
	protected DoProcessorResponseStatus validateDepositStage1(DoProcessorRequest request, DoProcessorResponse response) {
		boolean anyErrors = false;
		
		anyErrors &= FieldValidator.validateNumeric(AMOUNT_FIELD, STAGE_1, request, response);
		
		if (anyErrors) return DoProcessorResponseStatus.INPUTERROR;
			
		return DoProcessorResponseStatus.SUCCESS;
	}
	
	@Override
	protected DoProcessorResponseStatus depositStage1(DoProcessorRequest request, DoProcessorResponse response, DoProcessorContext context, RestTemplate rest) throws Exception {
		InitializeTransferRequest upayRequest = InitializeTransferRequest.builder()
			.amount(request.stageInputData(1, AMOUNT_FIELD))
			.description("Deposit for " + request.getUser().getFullName() + " (" + request.getUser().getUsername() + ")")
			.currency(request.getUser().getCurrency())
			.key(request.getProperty("apikey"))
			.orderId(request.getTransactionId().toString())
			.receiverAccount(request.getProperty("receiver_account"))
			.sender(request.stageInputData(1, "sender"))
			.build().sign(request.getProperty("apisecret"));
		
		log.info(upayRequest.toString());
		
		InitializeTransferResponse upayResponse = postForObject(request, response, context, rest,
			request.getProperty("apiurl") + "/api/merchant/v/1.0/function/initialize_transfer", 
			upayRequest, InitializeTransferResponse.class);
		
		if (!upayResponse.getStatus().equals("success")) {
			response.setMessage(upayResponse.getMsg());
			//TODO: The processor response can also follow the same recipe as pre-send validation of fields to populate the output fields for the stage
			buildRawResponseLog(response, upayResponse);
			return DoProcessorResponseStatus.INPUTERROR;
		}
		
		response.setOutputData(1, "account_info", request.stageInputData(1, "sender"));
		response.stageOutputData(1).put("hash", upayResponse.getHash());
		response.stageOutputData(1).put("order_id", upayResponse.getOrderId());
		response.stageOutputData(1).put("token_number", upayResponse.getTokenNumber());
		
		buildRawResponseLog(response, upayResponse);
		
		return DoProcessorResponseStatus.NEXTSTAGE;
	}
	
	@Override
	protected DoProcessorResponseStatus depositStage2(DoProcessorRequest request, DoProcessorResponse response, DoProcessorContext context, RestTemplate rest) throws Exception {
		FinishTransferRequest upayRequest = FinishTransferRequest.builder()
			.hash(request.stageOutputData(1, "hash"))
			.key(request.getProperty("apikey"))
			.receiverAccount(request.getProperty("receiver_account"))
			.tokenCode(request.stageInputData(2, "token_code"))
			.tokenNumber(request.stageOutputData(1, "token_number"))
			.build().sign(request.getProperty("apisecret"));
		log.info(upayRequest.toString());
		
		FinishTransferResponse upayResponse = postForObject(request, response, context, rest,
			request.getProperty("apiurl") + "/api/merchant/v/1.0/function/finish_transfer", 
			upayRequest, FinishTransferResponse.class);
		
		response.stageOutputData(2).put("transaction_id", upayResponse.getTransactionId());
		response.setProcessorReference(upayResponse.getTransactionId());
		
		buildRawResponseLog(response, upayResponse);
		
		if (upayResponse.getStatus().equals("error") && upayResponse.getCode().equals("517")) {
			return DoProcessorResponseStatus.NEXTSTAGE;
		}
		if (upayResponse.getStatus().equals("success")) {
			return DoProcessorResponseStatus.SUCCESS;
		}
		
		throw new DoErrorException(upayResponse.getMsg() + " " + upayResponse.getDescription());
	}

	@Override
	protected DoProcessorResponseStatus depositStage3(DoProcessorRequest request, DoProcessorResponse response, DoProcessorContext context, RestTemplate rest) throws Exception {
		GetTransactionStatusRequest upayRequest = GetTransactionStatusRequest.builder()
			.transactionId(request.stageOutputData(2, "transaction_id"))
			.key(request.getProperty("apikey"))
			.build().sign(request.getProperty("apisecret"));
		log.info(upayRequest.toString());
		
		GetTransactionStatusResponse upayResponse = postForObject(request, response, context, rest,
			request.getProperty("apiurl") + "/api/merchant/v/1.0/function/get_transaction_status", 
			upayRequest, GetTransactionStatusResponse.class);
		
		buildRawResponseLog(response, upayResponse);
		
		if (upayResponse.getStatus().equals("error") && upayResponse.getStatus().equals("517")) {
			return DoProcessorResponseStatus.NOOP;
		}
		if (upayResponse.getStatus().equals("success")) {
			if (upayResponse.getTransactionStatus().equals("C")) {
				return DoProcessorResponseStatus.SUCCESS;
			}
			if (upayResponse.getTransactionStatus().equals("R")) {
				return DoProcessorResponseStatus.DECLINED;
			}
			if (upayResponse.getTransactionStatus().equals("P")) {
				return DoProcessorResponseStatus.NOOP;
			}
			throw new DoErrorException("Invalid transaction status: " + upayResponse.getTransactionStatus());
		}
		
		throw new DoErrorException(upayResponse.getMsg() + " " + upayResponse.getTransactionStatusDescription());
	}

	@Override
	protected DoProcessorResponseStatus withdrawStage1(DoProcessorRequest request, DoProcessorResponse response, DoProcessorContext context, RestTemplate rest) throws Exception {
		TransferAccountToAccountRequest upayRequest = TransferAccountToAccountRequest.builder()
			.amount(request.stageInputData(1, AMOUNT_FIELD))
			.currency(request.getUser().getCurrency())
			.key(request.getProperty("apikey"))
			.test("0")
			.senderAccount(request.getProperty("receiver_account"))
			.receiverAccount(request.stageInputData(1, "receiver_account"))
			.build().sign(request.getProperty("apisecret"));
		log.info(upayRequest.toString());
		
		TransferAccountToAccountResponse upayResponse = postForObject(request, response, context, rest,
				request.getProperty("apiurl") + "/api/merchant/v/1.0/function/transfer_a_to_a", 
				upayRequest, TransferAccountToAccountResponse.class);
		
		if (!upayResponse.getStatus().equals("success")) {
			response.setMessage(upayResponse.getMsg());
			buildRawResponseLog(response, upayResponse);
			return DoProcessorResponseStatus.INPUTERROR;
		}
		
		response.setOutputData(1, "account_info", request.stageInputData(1, "receiver_account"));
		response.setOutputData(1, "transaction_id", upayResponse.getTransactionId());
		response.setProcessorReference(upayResponse.getTransactionId());
		
		buildRawResponseLog(response, upayResponse);
		
		return DoProcessorResponseStatus.SUCCESS;
	}
}
