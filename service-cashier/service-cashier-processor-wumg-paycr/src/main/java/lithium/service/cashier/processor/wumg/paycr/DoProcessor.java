package lithium.service.cashier.processor.wumg.paycr;

import java.io.StringReader;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.HtmlUtils;

import lithium.service.cashier.client.internal.DoProcessorRequest;
import lithium.service.cashier.client.internal.DoProcessorResponse;
import lithium.service.cashier.client.internal.DoProcessorResponseStatus;
import lithium.service.cashier.processor.DoProcessorContext;
import lithium.service.cashier.processor.wumg.paycr.data.CreateNewPayoutTransactionRequest;
import lithium.service.cashier.processor.wumg.paycr.data.CreateNewTransactionRequest;
import lithium.service.cashier.processor.wumg.paycr.data.GenericResponse;
import lithium.service.cashier.processor.wumg.paycr.data.GetAvailableReceiverRequest;
import lithium.service.cashier.processor.wumg.paycr.data.GetPayoutStatusRequest;
import lithium.service.cashier.processor.wumg.paycr.data.GetTransactionStatusRequest;
import lithium.service.cashier.processor.wumg.paycr.data.WebMethodResponse;
import lithium.service.cashier.processor.wumg.paycr.data.enums.PayCRCountryIDs;
import lithium.util.ObjectToHttpEntity;

@Service
public class DoProcessor extends DoProcessorWUMGAdapter {
	
	private WebMethodResponse parseResponse(String processorResponse) throws Exception {
		JAXBContext jc = JAXBContext.newInstance(GenericResponse.class);
		Unmarshaller u = jc.createUnmarshaller();
		String xml = HtmlUtils.htmlUnescape(processorResponse);
		GenericResponse rs = (GenericResponse) u.unmarshal( new StreamSource( new StringReader( xml ) ) );
		WebMethodResponse r = rs.getWebMethodResponse();
		return r;
	}
	
	private String getDocTypeId(DoProcessorRequest request) throws Exception {
		String docTypeId = "";
		if (request.getMethodCode().equals("mg")) docTypeId = "1";
		if (request.getMethodCode().equals("wu")) docTypeId = "2";
		if (docTypeId.isEmpty()) throw new Exception("Invalid method code");
		return docTypeId;
	}
	
	@Override
	protected DoProcessorResponseStatus depositStage1(DoProcessorRequest request, DoProcessorResponse response, DoProcessorContext context, RestTemplate rest) throws Exception {
		String docTypeId = getDocTypeId(request);
		
		GetAvailableReceiverRequest processorRequest = GetAvailableReceiverRequest.builder()
				.player(request.getUser().getFullName())
				.amount(new Long(Math.round(Double.parseDouble(request.stageInputData(1, "amount")))).toString())
				.companyId(request.getProperty("companyId"))
				.userName(request.getProperty("username"))
				.password(request.getProperty("password"))
				.docTypeId(docTypeId)
				.build();
		
		String processorResponse = postForObject(request, response, context, rest,
			request.getProperty("url")+"/GetAvailableReceiver",
			ObjectToHttpEntity.forPostFormFormParam(processorRequest), String.class);
		
		WebMethodResponse r = parseResponse(processorResponse);
		
		if (!r.getResponseStatus().equals("1000")) { 
			response.setMessage("Error " + r.getResponseStatus() + " " + r.getResponseDescription());
			buildRawResponseLog(response, r);
			return DoProcessorResponseStatus.FATALERROR;
		}
		
		response.stageOutputData(1).put("receiver_name", r.getReceiverName());
		response.stageOutputData(1).put("receiver_country", r.getCountry());
		response.stageOutputData(1).put("receiver_city", r.getCity() + ", " + r.getState());
		response.stageOutputData(1).put("name_id", r.getReceiverId());
		response.stageOutputData(1).put("temp_committed_id", r.getTempCommitedId());
		
		buildRawResponseLog(response, r);
		return DoProcessorResponseStatus.NEXTSTAGE;

	}
	
	@Override
	protected DoProcessorResponseStatus depositStage2(DoProcessorRequest request, DoProcessorResponse response, DoProcessorContext context, RestTemplate rest) throws Exception {
		PayCRCountryIDs countryId = PayCRCountryIDs.valueOf(request.getUser().getResidentialAddress().getCountryCode());
		
		CreateNewTransactionRequest processorRequest = CreateNewTransactionRequest.builder()
			.player(request.getUser().getFullName())
			.amount(new Long(Math.round(Double.parseDouble(request.stageInputData(1, "amount")))).toString())
			.companyId(request.getProperty("companyId"))
			.userName(request.getProperty("username"))
			.password(request.getProperty("password"))
			.sendername(request.getUser().getFullName())
			.trackingNumber(request.stageInputData(2, "control_number"))
			.receiverId(request.stageOutputData(1, "name_id"))
			.tempCommitedId(request.stageOutputData(1, "temp_committed_id"))
			.sendercity(request.getUser().getResidentialAddress().getCity())
			.senderstate(request.getUser().getResidentialAddress().getAdminLevel1())
			.sendercountryId(countryId.getId().toString())
			.senderaddress(
				request.getUser().getResidentialAddress().getAddressLine1() + ", " +
				request.getUser().getResidentialAddress().getAddressLine2() + ", " +
				request.getUser().getResidentialAddress().getAddressLine3()
			)
			.extTransId(request.getTransactionId().toString())
			.moneyChange("1")
			.build();
		
		response.setOutputData(2, "account_info", request.stageInputData(2, "control_number"));
		
		String processorResponse = postForObject(request, response, context, rest,
			request.getProperty("url") + "/CreateNewTransaction", 
			ObjectToHttpEntity.forPostFormFormParam(processorRequest), String.class);
		
		WebMethodResponse r = parseResponse(processorResponse);
		
		if (r.getTransactionId() != null) {
			response.setProcessorReference(r.getTransactionId());
			response.setOutputData(1, "transaction_id", r.getTransactionId());
		}

		if (!r.getResponseStatus().equals("1000")) { 
			response.setMessage("Error " + r.getResponseStatus() + " " + r.getResponseDescription());
			buildRawResponseLog(response, r);
			return DoProcessorResponseStatus.FATALERROR;
		}
		
		buildRawResponseLog(response, r);
		if (r.getTransactionStatusId().equals("1")) return DoProcessorResponseStatus.NEXTSTAGE; 
		if (r.getTransactionStatusId().equals("2")) return DoProcessorResponseStatus.SUCCESS; 
		if (r.getTransactionStatusId().equals("3")) return DoProcessorResponseStatus.DECLINED; 
		if (r.getTransactionStatusId().equals("6")) return DoProcessorResponseStatus.DECLINED; 
		
		throw new Exception("Invalid transaction status: " + r.getTransactionStatusId());

	}

	@Override
	protected DoProcessorResponseStatus depositStage3(DoProcessorRequest request, DoProcessorResponse response, DoProcessorContext context, RestTemplate rest) throws Exception {
		GetTransactionStatusRequest processorRequest = GetTransactionStatusRequest.builder()
				.extTransId(request.getTransactionId().toString())
				.build();
		
		String processorResponse = postForObject(request, response, context, rest,
			request.getProperty("url") + "/GetTransactionStatus", 
			ObjectToHttpEntity.forPostFormFormParam(processorRequest), String.class);
		
		WebMethodResponse r = parseResponse(processorResponse);
		if (r.getTransactionId() != null) response.setProcessorReference(r.getTransactionId());
		
		if (!r.getResponseStatus().equals("1000")) { 
			response.setMessage("Error " + r.getResponseStatus() + " " + r.getResponseDescription());
			buildRawResponseLog(response, r);
			return DoProcessorResponseStatus.FATALERROR;
		}
		
		buildRawResponseLog(response, r);
		if (r.getTransactionStatusId().equals("1")) return DoProcessorResponseStatus.NOOP; 
		if (r.getTransactionStatusId().equals("2")) return DoProcessorResponseStatus.SUCCESS; 
		if (r.getTransactionStatusId().equals("3")) return DoProcessorResponseStatus.DECLINED; 
		if (r.getTransactionStatusId().equals("6")) return DoProcessorResponseStatus.DECLINED; 
		
		throw new Exception("Invalid transaction status: " + r.getTransactionStatusId());
	}
	
	@Override
	protected DoProcessorResponseStatus withdrawStage1(DoProcessorRequest request, DoProcessorResponse response, DoProcessorContext context, RestTemplate rest) throws Exception {
		PayCRCountryIDs countryId = PayCRCountryIDs.valueOf(request.getUser().getResidentialAddress().getCountryCode());
		String docTypeId = getDocTypeId(request);
		
		CreateNewPayoutTransactionRequest processorRequest = CreateNewPayoutTransactionRequest.builder()
				.receiver(request.getUser().getFullName())
				.city(request.getUser().getResidentialAddress().getCity())
				.state(request.getUser().getResidentialAddress().getAdminLevel1())
				.countryId(countryId.getId().toString())
				.player(request.getUser().getGuid())
				.amount(new Long(Math.round(Double.parseDouble(request.stageInputData(1, "amount")))).toString())
				.companyId(request.getProperty("companyId"))
				.userName(request.getProperty("username"))
				.password(request.getProperty("password"))
				.docTypeId(docTypeId)
				.build();
		
		String processorResponse = postForObject(request, response, context, rest,
			request.getProperty("url") + "/CreateNewPayOutTransaction", 
			ObjectToHttpEntity.forPostFormFormParam(processorRequest), String.class);
		
//		response.setRawRequestLog(JsonStringify.objectToString(processorRequest));
		WebMethodResponse r = parseResponse(processorResponse);
		
		if (r.getTransactionId() != null) {
			response.setProcessorReference(r.getTransactionId());
			response.setOutputData(1, "transaction_id", r.getTransactionId());
		}
		
		if (!r.getResponseStatus().equals("1000")) { 
			response.setMessage("Error " + r.getResponseStatus() + " " + r.getResponseDescription());
			buildRawResponseLog(response, r);
			return DoProcessorResponseStatus.FATALERROR;
		}
		
		buildRawResponseLog(response, r);
		if (r.getTransactionStatusId().equals("1")) return DoProcessorResponseStatus.NEXTSTAGE; 
		if (r.getTransactionStatusId().equals("2")) return DoProcessorResponseStatus.SUCCESS; 
		if (r.getTransactionStatusId().equals("3")) return DoProcessorResponseStatus.DECLINED; 
		if (r.getTransactionStatusId().equals("6")) return DoProcessorResponseStatus.DECLINED; 
		
		throw new Exception("Invalid transaction status: " + r.getTransactionStatusId());
		
	}

	@Override
	protected DoProcessorResponseStatus withdrawStage2(DoProcessorRequest request, DoProcessorResponse response, DoProcessorContext context, RestTemplate rest) throws Exception {
		GetPayoutStatusRequest processorRequest = GetPayoutStatusRequest.builder()
				.transactionId(request.stageOutputData(1, "transaction_id"))
				.companyId(request.getProperty("companyId"))
				.userName(request.getProperty("username"))
				.password(request.getProperty("password"))
				.build();
		
		String processorResponse = postForObject(request, response, context, rest,
			request.getProperty("url") + "/GetPayOutStatus", 
			ObjectToHttpEntity.forPostFormFormParam(processorRequest), String.class);
		
		WebMethodResponse r = parseResponse(processorResponse);
		if (r.getTransactionId() != null) response.setProcessorReference(r.getTransactionId());
		
		if (!r.getResponseStatus().equals("1000")) { 
			response.setMessage("Error " + r.getResponseStatus() + " " + r.getResponseDescription());
			buildRawResponseLog(response, r);
			return DoProcessorResponseStatus.FATALERROR;
		}
		buildRawResponseLog(response, r);
		if (r.getTransactionStatusId().equals("1")) return DoProcessorResponseStatus.NOOP; 
		if (r.getTransactionStatusId().equals("2")) return DoProcessorResponseStatus.SUCCESS; 
		if (r.getTransactionStatusId().equals("3")) return DoProcessorResponseStatus.DECLINED; 
		if (r.getTransactionStatusId().equals("6")) return DoProcessorResponseStatus.DECLINED; 
		
		throw new Exception("Invalid transaction status: " + r.getTransactionStatusId());
	}
}
