package lithium.service.cashier.mock.wumg.paymentclicks.controllers;

import java.util.Date;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import lithium.service.cashier.processor.wumg.paymentclicks.data.EditDepositRequest;
import lithium.service.cashier.processor.wumg.paymentclicks.data.EditPayoutRequest;
import lithium.service.cashier.processor.wumg.paymentclicks.data.GetTransactionRequest;
import lithium.service.cashier.processor.wumg.paymentclicks.data.RequestNameRequest;
import lithium.service.cashier.processor.wumg.paymentclicks.data.RequestNameResponse;
import lithium.service.cashier.processor.wumg.paymentclicks.data.SubmitDepositRequest;
import lithium.service.cashier.processor.wumg.paymentclicks.data.SubmitPayoutRequest;
import lithium.service.cashier.processor.wumg.paymentclicks.data.TransactionResponse;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class APIController {

	@PostMapping(path="/api/request_name.php", produces=MediaType.APPLICATION_XML_VALUE)
	public RequestNameResponse requestName(RequestNameRequest request) {
		
		log.info("requestName " + request);

		if (request.getAmount() == null) {
			return RequestNameResponse.builder()
					.status("1000")
					.errorMessage("Invalid amount")
			.build();
		}
		
		return RequestNameResponse.builder()
				.version("1.0")
				.nameId("100")
				.processor("Western Union")
				.sender("Alberto Chan Lopez")
				.account("SY777")
				.receiverName("GERARDO VALVERDE ARAYA")
				.receiverCity("Santa Barbara, Heredia")
				.receiverCountry("CRC")
				.build();
		
	}

	@PostMapping(path="/api/submit_deposit.php", produces=MediaType.APPLICATION_XML_VALUE)
	public TransactionResponse submitDeposit(SubmitDepositRequest request) {

		log.info("submitDeposit " + request);

		return TransactionResponse.builder()
				.version("1.0")
				.id("100")
				.type("Deposit")
				.processor("Western Union")
				.senderName("Alberto Chan Lopez")
				.senderCountry("USA")
				.senderState("Alabama")
				.receiverName("GERARDO VALVERDE ARAYA")
				.receiverCity("Santa Barbara, Heredia")
				.receiverCountry("CRC")
				.amount(request.getAmount())
				.charge("100")
				.comments("No comments")
				.status("Pending")
				.controlNumber("123123")
				.fee("100")
				.feedback("No feedback")
				.pin("PIN")
				.processedDate(new Date().toString())
				.transactionDate(new Date().toString())
				.build();
		
	}

	@PostMapping(path="/api/edit_deposit.php", produces=MediaType.APPLICATION_XML_VALUE)
	public TransactionResponse editDeposit(EditDepositRequest request) {

		log.info("editDeposit " + request);

		return TransactionResponse.builder()
				.version("1.0")
				.id("100")
				.type("Deposit")
				.processor("Western Union")
				.senderName("Alberto Chan Lopez")
				.senderCountry("USA")
				.senderState("Alabama")
				.receiverName("GERARDO VALVERDE ARAYA")
				.receiverCity("Santa Barbara, Heredia")
				.receiverCountry("CRC")
				.amount(request.getAmount())
				.charge("100")
				.comments("No comments")
				.status("Pending")
				.controlNumber("123123")
				.fee("100")
				.feedback("No feedback")
				.pin("PIN")
				.processedDate(new Date().toString())
				.transactionDate(new Date().toString())
				.build();
		
	}

	@PostMapping(path="/api/submit_payout.php", produces=MediaType.APPLICATION_XML_VALUE)
	public TransactionResponse submitPayout(SubmitPayoutRequest request) {

		log.info("submitPayout " + request);

		return TransactionResponse.builder()
				.version("1.0")
				.id("100")
				.type("Payout")
				.processor("Western Union")
				.senderName("Alberto Chan Lopez")
				.senderCountry("USA")
				.senderState("Alabama")
				.receiverName("GERARDO VALVERDE ARAYA")
				.receiverCity("Santa Barbara, Heredia")
				.receiverCountry("CRC")
				.amount(request.getAmount())
				.charge("100")
				.comments("No comments")
				.status("Pending")
				.controlNumber("123123")
				.fee("100")
				.feedback("No feedback")
				.pin("PIN")
				.processedDate(new Date().toString())
				.transactionDate(new Date().toString())
				.build();
		
	}

	@PostMapping(path="/api/edit_payout.php", produces=MediaType.APPLICATION_XML_VALUE)
	public TransactionResponse editPayout(EditPayoutRequest request) {

		log.info("editPayout " + request);
		
		return TransactionResponse.builder()
				.version("1.0")
				.id("100")
				.type("Payout")
				.processor("Western Union")
				.senderName("Alberto Chan Lopez")
				.senderCountry("USA")
				.senderState("Alabama")
				.receiverName("GERARDO VALVERDE ARAYA")
				.receiverCity("Santa Barbara, Heredia")
				.receiverCountry("CRC")
				.amount(request.getAmount())
				.charge("100")
				.comments("No comments")
				.status("Pending")
				.controlNumber("123123")
				.fee("100")
				.feedback("No feedback")
				.pin("PIN")
				.processedDate(new Date().toString())
				.transactionDate(new Date().toString())
				.build();
		
	}

	@PostMapping(path="/api/get_transaction.php", produces=MediaType.APPLICATION_XML_VALUE)
	public TransactionResponse getTransaction(GetTransactionRequest request) {

		log.info("getTransaction " + request);

		return TransactionResponse.builder()
				.version("1.0")
				.id("100")
				.type("Payout")
				.processor("Western Union")
				.senderName("Alberto Chan Lopez")
				.senderCountry("USA")
				.senderState("Alabama")
				.receiverName("GERARDO VALVERDE ARAYA")
				.receiverCity("Santa Barbara, Heredia")
				.receiverCountry("CRC")
				.amount("10000")
				.charge("100")
				.comments("No comments")
				.status("Approved")
				.controlNumber("123123")
				.fee("100")
				.feedback("No feedback")
				.pin("PIN")
				.processedDate(new Date().toString())
				.transactionDate(new Date().toString())
				.build();
		
	}

}
