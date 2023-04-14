package lithium.service.cashier.mock.wumg.directeller.endpoint;

import java.util.Random;

import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import lithium.service.cashier.processor.wumg.directeller.wsdl.ConfirmDeposit;
import lithium.service.cashier.processor.wumg.directeller.wsdl.ConfirmDepositResponse;
import lithium.service.cashier.processor.wumg.directeller.wsdl.GetStatus;
import lithium.service.cashier.processor.wumg.directeller.wsdl.GetStatusResponse;
import lithium.service.cashier.processor.wumg.directeller.wsdl.InitDeposit;
import lithium.service.cashier.processor.wumg.directeller.wsdl.InitDepositResponse;
import lithium.service.cashier.processor.wumg.directeller.wsdl.InitPayout;
import lithium.service.cashier.processor.wumg.directeller.wsdl.InitPayoutResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Endpoint
public class TransactEndpoint {
	private static final String NAMESPACE_URI = "http://directteller.com/";
	
	@PayloadRoot(namespace=NAMESPACE_URI, localPart="init_deposit")
	@ResponsePayload
	public InitDepositResponse initDeposit(@RequestPayload InitDeposit request) {
		log.info("InitDeposit Endpoint received :: "+request);
		
		InitDepositResponse response = null;
		Random r = new Random();
		int random = r.nextInt(100);
		if (random < 90 || request.getTransactionAmount().equals("55.25")) {
			response = InitDepositResponse.builder()
				.initDepositResult(
					"<DirectTeller>"
					+ "<status>0</status>"
					+ "<datetime>11/3/2017 6:30:20 AM</datetime>"
					+ "<transaction_state>1</transaction_state>"
					+ "<trans_id>"+random+"</trans_id>"
					+ "<merchant_code>84</merchant_code>"
					+ "<receiver_code>0</receiver_code>"
					+ "<receiver_name>SANTOS ANIBAL ALVARADO LOPEZ</receiver_name>"
					+ "<receiver_city></receiver_city>"
					+ "<receiver_state>san jose</receiver_state>"
					+ "<receiver_country>Costa Rica</receiver_country>"
					+ "<receiver_country_code>CR</receiver_country_code>"
					+ "<external_trace_id>144</external_trace_id>"
					+ "</DirectTeller>"
				).build();
		} else {
			response = InitDepositResponse.builder()
				.initDepositResult(
					"<DirectTeller>"
					+ "<id>1000</id>"
					+ "<message>Description of the Message</message>"
					+ "<transaction_status>Error</transaction_status>"
					+ "</DirectTeller>"
				).build();
		}
		
		log.info("InitDeposit Endpoint sending :: "+response);
		return response;
	}
	
	@PayloadRoot(namespace=NAMESPACE_URI, localPart="confirm_deposit")
	@ResponsePayload
	public ConfirmDepositResponse confirmDeposit(@RequestPayload ConfirmDeposit request) {
		log.info("ConfirmDeposit Endpoint received :: "+request);
		
		ConfirmDepositResponse response = null;
		Random r = new Random();
		int random = r.nextInt(100);
		if (random <= 80 || request.getTransactionAmount().equals("25.25")) {
			response = ConfirmDepositResponse.builder()
				.confirmDepositResult(
					"<DirectTeller>"
					+ "<status>0</status>"
					+ "<transaction_state>Pending</transaction_state>"
					+ "<datetime>7/28/2010 10:00:00 AM</datetime>"
					+ "<trans_id>125</trans_id>"
					+ "<external_trace_id>1321</external_trace_id>"
					+ "</DirectTeller>"
				).build();
		} else {
			response = ConfirmDepositResponse.builder()
				.confirmDepositResult(
					"<DirectTeller>"
					+ "<id>1010</id>"
					+ "<message>Confirmation number already used</message>"
					+ "<transaction_status>Error</transaction_status>"
					+ "</DirectTeller>"
				).build();
		}
		
		log.info("ConfirmDeposit Endpoint sending :: "+response);
		return response;
	}
	
	@PayloadRoot(namespace=NAMESPACE_URI, localPart="get_status")
	@ResponsePayload
	public GetStatusResponse getStatus(@RequestPayload GetStatus request) {
		log.info("GetStatus Endpoint received :: "+request);
		
		GetStatusResponse response = null;
		Random r = new Random();
		int random = r.nextInt(100);
		if (random < 30) {
			response = GetStatusResponse.builder()
				.getStatusResult(
					"<DirectTeller>"
					+ "<status>0</status>"
					+ "<trans_id>125</trans_id>"
					+ "<customer_pin>2234</customer_pin>"
					+ "<transaction_state>Pending</transaction_state>"
					+ "<amount>200.00</amount>"
					+ "<currency>USD</currency>"
					+ "<transfer_fee>0.00</transfer_fee>"
					+ "<dmt_total_fees>5.00</dmt_total_fees>"
					+ "<comments></comments>"
					+ "<trans_control_number>44445555344</trans_control_number>"
					+ "<sender_beneficiary_info>PEREZ MOREIRA</sender_beneficiary_info>"
					+ "<sender_location>Costa Rica- San Jose</sender_location>"
					+ "<external_trace_id>1321</external_trace_id>"
					+ "<reject_code>0</reject_code>"
					+ "</DirectTeller>"
				).build();
		} else if ((random >= 30) && (random < 60)) {
			response = GetStatusResponse.builder()
				.getStatusResult(
					"<DirectTeller>"
					+ "<status>0</status>"
					+ "<trans_id>125</trans_id>"
					+ "<customer_pin>2234</customer_pin>"
					+ "<transaction_state>Rejected</transaction_state>"
					+ "<amount>200.00</amount>"
					+ "<currency>USD</currency>"
					+ "<transfer_fee>0.00</transfer_fee>"
					+ "<dmt_total_fees>5.00</dmt_total_fees>"
					+ "<comments></comments>"
					+ "<trans_control_number>44445555344</trans_control_number>"
					+ "<sender_beneficiary_info>PEREZ MOREIRA</sender_beneficiary_info>"
					+ "<sender_location>Costa Rica- San Jose</sender_location>"
					+ "<external_trace_id>1321</external_trace_id>"
					+ "<reject_code>78</reject_code>"
					+ "</DirectTeller>"
				).build();
		} else {
			response = GetStatusResponse.builder()
				.getStatusResult(
					"<DirectTeller>"
					+ "<status>0</status>"
					+ "<trans_id>125</trans_id>"
					+ "<customer_pin>2234</customer_pin>"
					+ "<transaction_state>Success</transaction_state>"
					+ "<amount>200.00</amount>"
					+ "<currency>USD</currency>"
					+ "<transfer_fee>0.00</transfer_fee>"
					+ "<dmt_total_fees>5.00</dmt_total_fees>"
					+ "<comments></comments>"
					+ "<trans_control_number>44445555344</trans_control_number>"
					+ "<sender_beneficiary_info>PEREZ MOREIRA</sender_beneficiary_info>"
					+ "<sender_location>Costa Rica- San Jose</sender_location>"
					+ "<external_trace_id>1321</external_trace_id>"
					+ "<reject_code>0</reject_code>"
					+ "</DirectTeller>"
				).build();
		}
		
		log.info("GetStatus Endpoint sending :: "+response);
		return response;
	}
	
	@PayloadRoot(namespace=NAMESPACE_URI, localPart="init_payout")
	@ResponsePayload
	public InitPayoutResponse initPayout(@RequestPayload InitPayout request) {
		log.info("InitPayout Endpoint received :: "+request);
		
		InitPayoutResponse response = null;
		Random r = new Random();
		int random = r.nextInt(100);
		if (random <= 80 || request.getTransactionAmount().equals("25.25")) {
			response = InitPayoutResponse.builder()
				.initPayoutResult(
					"<DirectTeller>"
					+ "<status>0</status>"
					+ "<datetime>8/18/2010 12:19:00 PM</datetime>"
					+ "<transaction_state>Pending</transaction_state>"
					+ "<trans_id>1225</trans_id>"
					+ "<merchant_code>9048</merchant_code>"
					+ "<sender_name>Marco Ramirez</sender_name>"
					+ "<sender_city>Managua</sender_city>"
					+ "<sender_state></sender_state>"
					+ "<sender_country>Nicaragua</sender_country>"
					+ "<sender_country_code>NI</sender_country_code>"
					+ "<external_trace_id>1321</external_trace_id>"
					+ "</DirectTeller>"
				).build();
		} else {
			response = InitPayoutResponse.builder()
				.initPayoutResult(
					"<DirectTeller>"
					+ "<id>1018</id>"
					+ "<message>Insufficient Funds</ message>"
					+ "<transaction_status>Error</transaction_status>"
					+ "</DirectTeller>"
				).build();
		}
		
		log.info("InitPayout Endpoint sending :: "+response);
		return response;
	}
}
