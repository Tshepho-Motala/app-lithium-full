package lithium.service.cashier.processor.wumg.paymentclicks;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import lithium.service.cashier.client.internal.DoProcessorRequest;
import lithium.service.cashier.client.internal.DoProcessorResponse;
import lithium.service.cashier.client.internal.DoProcessorResponseStatus;
import lithium.service.cashier.processor.DoProcessorContext;
import lithium.service.cashier.processor.wumg.paymentclicks.data.GetTransactionRequest;
import lithium.service.cashier.processor.wumg.paymentclicks.data.RequestNameRequest;
import lithium.service.cashier.processor.wumg.paymentclicks.data.RequestNameResponse;
import lithium.service.cashier.processor.wumg.paymentclicks.data.SubmitDepositRequest;
import lithium.service.cashier.processor.wumg.paymentclicks.data.SubmitPayoutRequest;
import lithium.service.cashier.processor.wumg.paymentclicks.data.TransactionResponse;
import lithium.util.ObjectToHttpEntity;

@Service
public class DoProcessor extends DoProcessorWUMGAdapter {

	@Override
	protected DoProcessorResponseStatus depositStage1(DoProcessorRequest request, DoProcessorResponse response, DoProcessorContext context, RestTemplate rest) throws Exception {
		RequestNameRequest processorRequest = RequestNameRequest.builder()
			.sender(request.getUser().getFullName())
			.account(request.getUser().getGuid())
			.amount(new Long(Math.round(Double.parseDouble(request.stageInputData(1, "amount")))).toString())
			.user(request.getProperty("username"))
			.password(request.getProperty("password"))
			.processor(request.getMethodCode().toUpperCase())
			.build();
		
		RequestNameResponse processorResponse = postForObject(request, response, context, rest,
			request.getProperty("url") + "/request_name.php", 
			ObjectToHttpEntity.forPostForm(processorRequest), RequestNameResponse.class);
		
		
		if (processorResponse.getErrorMessage() != null) {
			response.setMessage(processorResponse.getErrorMessage());
			buildRawResponseLog(response, processorResponse);
			return DoProcessorResponseStatus.INPUTERROR;
		}
		
		response.stageOutputData(1).put("receiver_name", processorResponse.getReceiverName());
		response.stageOutputData(1).put("receiver_country", processorResponse.getReceiverCountry());
		response.stageOutputData(1).put("receiver_city", processorResponse.getReceiverCity());
		response.stageOutputData(1).put("name_id", processorResponse.getNameId());
		
		buildRawResponseLog(response, processorResponse);
		
		return DoProcessorResponseStatus.NEXTSTAGE;

	}
	
	@Override
	protected DoProcessorResponseStatus depositStage2(DoProcessorRequest request, DoProcessorResponse response, DoProcessorContext context, RestTemplate rest) throws Exception {
		SubmitDepositRequest processorRequest = SubmitDepositRequest.builder()
			.user(request.getProperty("username"))
			.password(request.getProperty("password"))
			.name(request.stageOutputData(1, "name_id"))
			.sender_name(request.getUser().getFullName())
			.sender_pin(request.getUser().getGuid())
			.amount(new Long(Math.round(Double.parseDouble(request.stageInputData(1, "amount")))).toString())
			.control_number(request.stageInputData(2, "control_number"))
			.sender_city(request.getUser().getResidentialAddress().getCity())
			.sender_state(request.getUser().getResidentialAddress().getAdminLevel1())
			.sender_country(request.getUser().getResidentialAddress().getCountryCode())
			.build();
		
		TransactionResponse processorResponse = postForObject(request, response, context, rest,
			request.getProperty("url") + "/submit_deposit.php", 
			ObjectToHttpEntity.forPostForm(processorRequest), TransactionResponse.class);
		
		if (processorResponse.getErrorMessage() != null) {
			response.setMessage(processorResponse.getErrorMessage());
			buildRawResponseLog(response, processorResponse);
			return DoProcessorResponseStatus.INPUTERROR;
		}
		
		response.setOutputData(2, "account_info", request.stageInputData(2, "control_number"));
		response.setOutputData(2, "transaction_id", processorResponse.getId());
		response.setProcessorReference(processorResponse.getId());
		response.setMessage(processorResponse.getFeedback());
		
		buildRawResponseLog(response, processorResponse);
		
		if (processorResponse.getStatus().equals("Approved")) {
			return DoProcessorResponseStatus.SUCCESS;
		}

		if (processorResponse.getStatus().equals("Rejected")) {
			return DoProcessorResponseStatus.DECLINED;
		}

		if (processorResponse.getStatus().equals("Cancelled")) {
			return DoProcessorResponseStatus.DECLINED;
		}
		return DoProcessorResponseStatus.NEXTSTAGE;
	}

	@Override
	protected DoProcessorResponseStatus depositStage3(DoProcessorRequest request, DoProcessorResponse response, DoProcessorContext context, RestTemplate rest) throws Exception {
		return getTransaction(request, response, context, rest, request.stageOutputData(2, "transaction_id"));
	}

	@Override
	protected DoProcessorResponseStatus withdrawStage1(DoProcessorRequest request, DoProcessorResponse response, DoProcessorContext context, RestTemplate rest) throws Exception {
		SubmitPayoutRequest processorRequest = SubmitPayoutRequest.builder()
			.user(request.getProperty("username"))
			.password(request.getProperty("password"))
			.amount(new Long(Math.round(Double.parseDouble(request.stageInputData(1, "amount")))).toString())
			.receiver_pin(request.getUser().getGuid())
			.receiver_name(request.getUser().getFullName())
			.receiver_city(request.getUser().getResidentialAddress().getCity())
			.receiver_state(request.getUser().getResidentialAddress().getAdminLevel1())
			.receiver_country(request.getUser().getResidentialAddress().getCountryCode())
			.build();
		
		TransactionResponse processorResponse = postForObject(request, response, context, rest,
			request.getProperty("url") + "/submit_deposit.php", 
			ObjectToHttpEntity.forPostForm(processorRequest), TransactionResponse.class);
		
		if (processorResponse.getErrorMessage() != null) {
			response.setMessage(processorResponse.getErrorMessage());
			buildRawResponseLog(response, processorResponse);
			return DoProcessorResponseStatus.INPUTERROR;
		}
		
		response.setOutputData(1, "transaction_id", processorResponse.getId());
		response.setProcessorReference(processorResponse.getId());
		response.setMessage(processorResponse.getFeedback());
		
		buildRawResponseLog(response, processorResponse);
		
		if (processorResponse.getStatus().equals("Approved")) {
			return DoProcessorResponseStatus.SUCCESS;
		}

		if (processorResponse.getStatus().equals("Rejected")) {
			return DoProcessorResponseStatus.DECLINED;
		}

		if (processorResponse.getStatus().equals("Cancelled")) {
			return DoProcessorResponseStatus.DECLINED;
		}
		return DoProcessorResponseStatus.NEXTSTAGE;
	}

	@Override
	protected DoProcessorResponseStatus withdrawStage2(DoProcessorRequest request, DoProcessorResponse response, DoProcessorContext context, RestTemplate rest) throws Exception {
		return getTransaction(request, response, context, rest, request.stageOutputData(1, "transaction_id"));
	}
	
	private DoProcessorResponseStatus getTransaction(DoProcessorRequest request, DoProcessorResponse response, DoProcessorContext context, RestTemplate rest, String transactionId) throws Exception {
		GetTransactionRequest processorRequest = GetTransactionRequest.builder()
				.user(request.getProperty("username"))
				.password(request.getProperty("password"))
				.transaction_id(transactionId)
				.build();
		
		TransactionResponse processorResponse = postForObject(request, response, context, rest,
			request.getProperty("url") + "/get_transaction.php", 
			ObjectToHttpEntity.forPostForm(processorRequest), TransactionResponse.class);
		
		if (processorResponse.getErrorMessage() != null) {
			response.setMessage(processorResponse.getErrorMessage());
			buildRawResponseLog(response, processorResponse);
			return DoProcessorResponseStatus.INPUTERROR;
		}
		
		response.setProcessorReference(processorResponse.getId());
		response.setMessage(processorResponse.getFeedback());
		
		buildRawResponseLog(response, processorResponse);
		
		switch (processorResponse.getStatus()) {
			case "Approved":
				return DoProcessorResponseStatus.SUCCESS;
			case "Rejected":
			case "Cancelled":
				return DoProcessorResponseStatus.DECLINED;
			default:
				return DoProcessorResponseStatus.NOOP;
		}
	}
}
