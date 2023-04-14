package lithium.service.cashier.processor.wumg.directeller;

import java.math.BigDecimal;
import java.text.DecimalFormat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.HtmlUtils;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;

import lithium.service.cashier.client.internal.DoProcessorRequest;
import lithium.service.cashier.client.internal.DoProcessorResponse;
import lithium.service.cashier.client.internal.DoProcessorResponseStatus;
import lithium.service.cashier.processor.DoProcessorContext;
import lithium.service.cashier.processor.DoProcessorWSAdapter;
import lithium.service.cashier.processor.wumg.directeller.ws.TransactClient;
import lithium.service.cashier.processor.wumg.directeller.wsdl.ConfirmDeposit;
import lithium.service.cashier.processor.wumg.directeller.wsdl.ConfirmDepositResponse;
import lithium.service.cashier.processor.wumg.directeller.wsdl.ConfirmDepositResponseDT;
import lithium.service.cashier.processor.wumg.directeller.wsdl.GetStatus;
import lithium.service.cashier.processor.wumg.directeller.wsdl.GetStatusResponse;
import lithium.service.cashier.processor.wumg.directeller.wsdl.GetStatusResponseDT;
import lithium.service.cashier.processor.wumg.directeller.wsdl.InitDeposit;
import lithium.service.cashier.processor.wumg.directeller.wsdl.InitDepositResponse;
import lithium.service.cashier.processor.wumg.directeller.wsdl.InitDepositResponseDT;
import lithium.service.cashier.processor.wumg.directeller.wsdl.InitPayout;
import lithium.service.cashier.processor.wumg.directeller.wsdl.InitPayoutResponse;
import lithium.service.cashier.processor.wumg.directeller.wsdl.InitPayoutResponseDT;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class DoProcessor extends DoProcessorWSAdapter {
	@Autowired
	private TransactClient transactClient;
	
	private String paymentMethod(DoProcessorRequest request) throws Exception {
		String paymentMethod = "";
		if (request.getMethodCode().equals("wu")) paymentMethod = "1";
		if (request.getMethodCode().equals("mg")) paymentMethod = "2";
		if (paymentMethod.isEmpty()) throw new Exception("Invalid method code");
		return paymentMethod;
	}
	
	private XmlMapper xmlMapper() {
		JodaModule module = new JodaModule();
//		DateTimeFormatterFactory formatterFactory = new DateTimeFormatterFactory();
//		formatterFactory.setPattern("M/d/y h:m:s a"); //8/18/2010 12:19:00 PM
//		JacksonJodaDateFormat jacksonJodaDateFormat = new JacksonJodaDateFormat(formatterFactory.createDateTimeFormatter());
//		DateTimeDeserializer deserializer = new DateTimeDeserializer(DateTime.class, jacksonJodaDateFormat);
//		module.addDeserializer(DateTime.class, deserializer.forType(DateTime.class));
		XmlMapper mapper = new XmlMapper();
		mapper.registerModule(module);
		return mapper;
	}
	
	@Override
	protected DoProcessorResponseStatus depositStage1(DoProcessorRequest request, DoProcessorResponse response, DoProcessorContext context, RestTemplate rest) throws Exception {
		String paymentMethod = paymentMethod(request);
		String url = request.getProperty("url");
		String username = request.getProperty("username");
		String password = request.getProperty("password");
		String transferType = request.getProperty("transferType");
		Integer customerId = Integer.parseInt(request.getProperty("customerId"));
		DecimalFormat df = new DecimalFormat("#0.00");
		
		InitDeposit processorRequest = InitDeposit.builder()
			.username(username)
			.password(password)
			.paymentMethod(paymentMethod)
			.transferType(transferType)
			.customerID(customerId)
			.senderFirstName(request.getUser().getFirstName())
			.senderLastName(request.getUser().getLastName())
			.senderPhone(request.getUser().getTelephoneNumber())
			.senderCity(request.getUser().getResidentialAddress().getCity())
			.senderState(request.getUser().getResidentialAddress().getAdminLevel1Code())
			.senderCountry(request.getUser().getResidentialAddress().getCountryCode())
			.transactionAmount(df.format(new BigDecimal(request.stageInputData(1, "amount")).doubleValue()))
			.transactionCurrency(request.getUser().getCurrency())
			.userCreditCard(false)
			.externalTraceID(request.getTransactionId().toString())
			.build();
		log.info("InitDeposit :: "+processorRequest);
		
		InitDepositResponse initDepositResponse = transactClient.initDeposit(url, processorRequest);
		log.debug("InitDepositResponse :: "+initDepositResponse);
		
		buildRawRequestLog(request, response, processorRequest);
		
		String xml = HtmlUtils.htmlUnescape("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+initDepositResponse.getInitDepositResult());
		log.info("xml :: "+xml);
		InitDepositResponseDT processorResponse = xmlMapper().readValue(xml, InitDepositResponseDT.class);
		log.info("processorResponse :: "+processorResponse);
		
		if ((processorResponse.getStatus()==null) || (!processorResponse.getStatus().equals("0"))) {
			response.setMessage("Error " + processorResponse.getId()+" "+processorResponse.getMessage());
			buildRawResponseLog(response, processorResponse);
			return DoProcessorResponseStatus.DECLINED;
		}
		
		response.stageOutputData(1).put("receiver_name", processorResponse.getReceiverName());
		response.stageOutputData(1).put("receiver_country", processorResponse.getReceiverCountry());
		response.stageOutputData(1).put("receiver_city", processorResponse.getReceiverCity() + ", " + processorResponse.getReceiverState());
		response.stageOutputData(1).put("receiver_code", processorResponse.getReceiverCode());
		response.stageOutputData(1).put("trans_id", processorResponse.getTransactionId());
		
		buildRawResponseLog(response, processorResponse);
		return DoProcessorResponseStatus.NEXTSTAGE;
	}
	
	@Override
	protected DoProcessorResponseStatus depositStage2(DoProcessorRequest request, DoProcessorResponse response, DoProcessorContext context, RestTemplate rest) throws Exception {
		String url = request.getProperty("url");
		String username = request.getProperty("username");
		String password = request.getProperty("password");
		String customerId = request.getProperty("customerId");
		String transId = request.stageOutputData(1, "trans_id");
		DecimalFormat df = new DecimalFormat("#0.00");
		
		ConfirmDeposit processorRequest = ConfirmDeposit.builder()
			.username(username)
			.password(password)
			.customerID(customerId)
			.transactionID(transId)
			.merchantCustomerPin(request.getUser().getGuid())
			.senderFirstName(request.getUser().getFirstName())
			.senderLastName(request.getUser().getLastName())
			.senderPhone(request.getUser().getTelephoneNumber())
			.senderCity(request.getUser().getResidentialAddress().getCity())
			.senderState(request.getUser().getResidentialAddress().getAdminLevel1Code())
			.senderCountry(request.getUser().getResidentialAddress().getCountryCode())
			.transactionAmount(df.format(new BigDecimal(request.stageInputData(1, "amount")).doubleValue()))
			.controlNumber(request.stageInputData(2, "control_number"))
			.externalTraceID(request.getTransactionId().toString())
			.build();
		log.info("ConfirmDeposit :: "+processorRequest);
		
		response.setOutputData(2, "account_info", request.stageInputData(2, "control_number"));
		
		ConfirmDepositResponse confirmDepositResponse = transactClient.confirmDeposit(url, processorRequest);
		log.info("ConfirmDepositResponse :: "+confirmDepositResponse);
		
		buildRawRequestLog(request, response, processorRequest);
		
		String xml = HtmlUtils.htmlUnescape("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+confirmDepositResponse.getConfirmDepositResult());
		log.info("xml :: "+xml);
		ConfirmDepositResponseDT processorResponse = xmlMapper().readValue(xml, ConfirmDepositResponseDT.class);
		log.info("processorResponse :: "+processorResponse);
		
		if ((processorResponse.getStatus()==null) || (!processorResponse.getStatus().equals("0"))) {
			response.setMessage("Error " + processorResponse.getId()+" "+processorResponse.getMessage());
			buildRawResponseLog(response, processorResponse);
			return DoProcessorResponseStatus.DECLINED;
		}
		buildRawResponseLog(response, processorResponse);
		return DoProcessorResponseStatus.NEXTSTAGE;
	}
	
	@Override
	protected DoProcessorResponseStatus depositStage3(DoProcessorRequest request, DoProcessorResponse response, DoProcessorContext context, RestTemplate rest) throws Exception {
		String url = request.getProperty("url");
		String username = request.getProperty("username");
		String password = request.getProperty("password");
		String customerId = request.getProperty("customerId");
		String transId = request.stageOutputData(1, "trans_id");
		
		GetStatus processorRequest = GetStatus.builder()
			.username(username)
			.password(password)
			.customerID(customerId)
			.transactionID(transId)
			.build();
		log.info("GetStatus :: "+processorRequest);
		GetStatusResponse getStatusResponse = transactClient.getStatus(url, processorRequest);
		log.info("GetStatusResponse :: "+getStatusResponse);
		
		buildRawRequestLog(request, response, processorRequest);
		
		String xml = HtmlUtils.htmlUnescape("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+getStatusResponse.getGetStatusResult());
		log.info("xml :: "+xml);
		GetStatusResponseDT processorResponse = xmlMapper().readValue(xml, GetStatusResponseDT.class);
		log.info("processorResponse :: "+processorResponse);
		buildRawResponseLog(response, processorResponse);
		
		if (processorResponse.getTransactionState().equalsIgnoreCase("Pending")) return DoProcessorResponseStatus.NOOP;
		if (processorResponse.getTransactionState().equalsIgnoreCase("Success")) return DoProcessorResponseStatus.SUCCESS;
		if (processorResponse.getTransactionState().equalsIgnoreCase("Rejected")) return DoProcessorResponseStatus.DECLINED;
		throw new Exception("Invalid transaction status: " + processorResponse.getTransactionState());
	}
	
	@Override
	protected DoProcessorResponseStatus withdrawStage1(DoProcessorRequest request, DoProcessorResponse response, DoProcessorContext context, RestTemplate rest) throws Exception {
		String paymentMethod = paymentMethod(request);
		String url = request.getProperty("url");
		String username = request.getProperty("username");
		String password = request.getProperty("password");
		String transferType = request.getProperty("transferType");
		Integer customerId = Integer.parseInt(request.getProperty("customerId"));
		DecimalFormat df = new DecimalFormat("#0.00");
		
		InitPayout processorRequest = InitPayout.builder()
			.username(username)
			.password(password)
			.paymentMethod(paymentMethod)
			.transferType(transferType)
			.customerID(customerId+"")
			.receiverFirstName(request.getUser().getFirstName())
			.receiverLastName(request.getUser().getLastName())
			.receiverPhoneNumber(request.getUser().getTelephoneNumber())
			.receiverCity(request.getUser().getResidentialAddress().getCity())
			.receiverState(request.getUser().getResidentialAddress().getAdminLevel1Code())
			.receiverCountry(request.getUser().getResidentialAddress().getCountryCode())
			.transactionAmount(df.format(new BigDecimal(request.stageInputData(1, "amount")).doubleValue()))
			.comments("")
			.transactionCurrency(request.getUser().getCurrency())
			.externalTraceID(request.getTransactionId().toString())
			.build();
		log.info("InitPayout :: "+processorRequest);
		
		InitPayoutResponse initPayoutResponse = transactClient.initPayout(url, processorRequest);
		log.info("InitPayoutResponse :: "+initPayoutResponse);
		
		buildRawRequestLog(request, response, processorRequest);
		
		String xml = HtmlUtils.htmlUnescape("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+initPayoutResponse.getInitPayoutResult());
		log.info("xml :: "+xml);
		InitPayoutResponseDT processorResponse = xmlMapper().readValue(xml, InitPayoutResponseDT.class);
		log.info("processorResponse :: "+processorResponse);
		
		if ((processorResponse.getStatus()==null) || (!processorResponse.getStatus().equals("0"))) {
			response.setMessage("Error " + processorResponse.getId()+" "+processorResponse.getMessage());
			buildRawResponseLog(response, processorResponse);
			return DoProcessorResponseStatus.DECLINED;
		}
		buildRawResponseLog(response, processorResponse);
		return DoProcessorResponseStatus.SUCCESS;
	}
}