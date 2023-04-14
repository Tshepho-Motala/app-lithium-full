package lithium.service.accounting.provider.internal.controllers;

import lithium.service.Response;
import lithium.service.accounting.client.AccountingTransactionClient;
import lithium.service.accounting.client.AccountingTransactionLabelClient;
import lithium.service.accounting.exceptions.Status414AccountingTransactionDataValidationException;
import lithium.service.accounting.objects.CompleteTransaction;
import lithium.service.accounting.objects.TransactionLabelContainer;
import lithium.service.accounting.provider.internal.data.entities.Transaction;
import lithium.service.accounting.provider.internal.data.entities.TransactionComment;
import lithium.service.accounting.provider.internal.data.entities.TransactionEntry;
import lithium.service.accounting.provider.internal.data.entities.TransactionLabelValue;
import lithium.service.accounting.provider.internal.services.TransactionEntryService;
import lithium.service.accounting.provider.internal.services.TransactionService;
import lithium.service.client.datatable.DataTableRequest;
import lithium.service.client.datatable.DataTableResponse;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/transaction")
@Slf4j
public class TransactionController implements AccountingTransactionClient, AccountingTransactionLabelClient {

	@Autowired TransactionService transactionService;
	@Autowired TransactionEntryService transactionEntryService;
	@Autowired ModelMapper mapper;

	@RequestMapping("/begin")
	public ResponseEntity<Transaction> beginTransaction(
			@RequestParam String transactionTypeCode,
			@RequestParam String authorGuid) throws Status414AccountingTransactionDataValidationException {
		return new ResponseEntity<Transaction>(transactionService.beginTransaction(transactionTypeCode, authorGuid), HttpStatus.OK);
	}
	
	@RequestMapping("/{transactionId}")
	public ResponseEntity<List<TransactionEntry>> get(
		@PathVariable Long transactionId
	) {
		ResponseEntity<List<TransactionEntry>> listResponseEntity = new ResponseEntity<>(transactionService.find(transactionId), HttpStatus.OK);
		return listResponseEntity;
	}
	
	@RequestMapping("/{transactionId}/label")
	public ResponseEntity<TransactionLabelValue> label(
			@PathVariable Long transactionId, 
			@RequestParam String key, 
			@RequestParam String value) {
		return new ResponseEntity<TransactionLabelValue>(transactionService.label(transactionId, key, value), HttpStatus.OK);
	}

	@RequestMapping("/{transactionId}/comment")
	public ResponseEntity<TransactionComment> comment(
			@PathVariable Long transactionId,
			@RequestParam String comment) {
		return new ResponseEntity<TransactionComment>(transactionService.comment(transactionId, comment), HttpStatus.OK);
	}

	@RequestMapping("/{transactionId}/adjust")
	public ResponseEntity<TransactionEntry> adjust(
			@PathVariable Long transactionId, 
			@RequestParam String accountCode,
			@RequestParam String accountTypeCode,
			@RequestParam String currencyCode, 
			@RequestParam Long amountCents,
			@RequestParam @DateTimeFormat(iso=ISO.DATE_TIME) DateTime date,
			@RequestParam String domainName, 
			@RequestParam String accountOwnerGuid) {

		return new ResponseEntity<TransactionEntry>(
				transactionService.adjust(transactionId, 
						accountCode, accountTypeCode, currencyCode, 
						amountCents, date, domainName, accountOwnerGuid), HttpStatus.OK);
	}
	
	@RequestMapping("/findPlayerTransactionsForDateRangeAndUserGuid")
	public DataTableResponse<lithium.service.accounting.objects.TransactionEntry> findPlayerTransactionsForDateRangeAndUserGuid(
			@RequestParam("startDate") String startDate, 
			@RequestParam("endDate") String endDate, 
			@RequestParam("userGuid") String userGuid,
			@RequestParam("draw") String draw,
			@RequestParam("start") String start,
			@RequestParam("length") String length,
			@ModelAttribute DataTableRequest request) throws Exception {
		
		DataTableResponse<lithium.service.accounting.objects.TransactionEntry> response = transactionService.getTransactionsForDateRangeAndUserGuid(request, new DateTime(startDate), new DateTime(endDate), userGuid);

		return response;
	}
	
	@RequestMapping("/findLabelsForTransaction")
	public List<lithium.service.accounting.objects.LabelValue> findLabelsForTransaction(
			@RequestParam("tranId") Long tranId) throws Exception {
		return transactionService.findLabelsForTransaction(tranId);
	}
	
	@RequestMapping("/addLabels")
	public void addLabels(@RequestBody TransactionLabelContainer labelContainer) throws Exception {
		log.debug("Going to add labels from the following: " + labelContainer.toString());
		transactionService.summarizeAdditionalTransactionLabels(labelContainer);
		log.debug("Completed the summary addition call");
	}
	
	@RequestMapping("/findByLabelConstraint")
	public Response<CompleteTransaction> findByLabelConstraint(String domainName, 
			String ownerGuid,
			String currencyCode,
			String labelName,
			String labelValue,
			String originalAccountCode,
			String originalAccountTypeCode) throws Exception {
		return transactionService.findByLabelConstraint(domainName, ownerGuid, currencyCode, labelName, labelValue, originalAccountCode, originalAccountTypeCode);
	}
	
	@RequestMapping("/findByLabelConstraintList")
	public Response<List<CompleteTransaction>> findByLabelConstraintList(
			@RequestParam("domainName") String domainName, 
			@RequestParam("ownerGuid") String ownerGuid,
			@RequestParam("currencyCode") String currencyCode,
			@RequestParam("labelName") String labelName,
			@RequestParam("labelValue") String labelValue,
			@RequestParam("originalAccountCodeList") List<String> originalAccountCodeList,
			@RequestParam("originalAccountTypeCodeList") List<String> originalAccountTypeCodeList
			) throws Exception {
		return transactionService.findByLabelConstraintList(domainName, ownerGuid, currencyCode, labelName, labelValue, originalAccountCodeList, originalAccountTypeCodeList);
	}

	@RequestMapping("/find-external-transaction-id")
	public Long findExternalTransactionId(@RequestParam("externalTransactionId") String externalTransactionId, @RequestParam("transactionTypeCode") String transactionTypeCode) {
		return transactionService.findExternalTransactionId(externalTransactionId, "transaction_id", transactionTypeCode);
	}

	@RequestMapping("/find-external-reverse-transaction-id")
	public Long findExternalReverseTransactionId(@RequestParam("externalTransactionId") String externalTransactionId) {
		return transactionService.findExternalTransactionId(externalTransactionId, "transaction_id", "TRANSFER_FROM_PLAYER_BALANCE_PENDING_WITHDRAWAL");
	}

	@RequestMapping("/find-labels-by-external-transaction")
	public TransactionLabelContainer findLabelsByExternalTransaction(@RequestParam("exTranId") String externalTransactionId, @RequestParam("typeCode")  String transactionTypeCode) {
		Long transactionId = transactionService.findExternalTransactionId(externalTransactionId, "transaction_id", transactionTypeCode);
		if (transactionId != null) {
			return  TransactionLabelContainer.builder()
					.transactionId(transactionId)
					.labelList(transactionService.getLabelsForTransaction(transactionId))
					.build();
		}
		return null;
	}

	@RequestMapping("/is-used-free-bet")
	public Boolean isUsedFreeBet(
			@RequestParam("guid") String guid,
			@RequestParam("currency") String currencyCode,
			@RequestParam("accountCode") String accountCodeName,
			@RequestParam("accountType") String accountTypeName
			) {
		return transactionService.isUsedFreeBet(guid, currencyCode, accountCodeName, accountTypeName);
	}
}
