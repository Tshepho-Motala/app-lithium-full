package lithium.service.accounting.provider.internal.controllers;

import lithium.service.Response;
import lithium.service.Response.Status;
import lithium.service.accounting.provider.internal.data.entities.TransactionType;
import lithium.service.accounting.provider.internal.services.TransactionTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/transactiontype")
public class TransactionTypeController {

	@Autowired
	TransactionTypeService transactionTypeService;

	@RequestMapping("/create")
	public Response<TransactionType> create(String code) {
		TransactionType t = transactionTypeService.findOrCreate(code);
		return Response.<TransactionType>builder().data(t).status(Status.OK).build();
	}

	@RequestMapping("/find")
	public Response<TransactionType> findByCode(String code) {
		TransactionType tt = transactionTypeService.findByCode(code);
		if (tt == null) return Response.<TransactionType>builder().status(Status.NOT_FOUND).build();
		return Response.<TransactionType>builder().data(tt).build();
	}

	@RequestMapping("/{id}/addlabel")
	public void addLabel(
			@PathVariable Long id,
			@RequestParam("label") String label, 
			@RequestParam("summarize") boolean summarize,
			@RequestParam(name = "summarizeTotal", required = false) Boolean summarizeTotal,
			@RequestParam(name = "synchronous", required = false) Boolean synchronous) {
		TransactionType tt = transactionTypeService.findOne(id);
		transactionTypeService.addLabel(tt, label, summarize, summarizeTotal, synchronous);
	}
	
	@RequestMapping("/{id}/addoptionallabel")
	public void addOptionalLabel(
			@PathVariable Long id,
			@RequestParam("label") String label,
			@RequestParam("summarize") boolean summarize,
			@RequestParam(name = "summarizeTotal", required = false) Boolean summarizeTotal,
			@RequestParam(name = "synchronous", required = false) Boolean synchronous) {
		TransactionType tt = transactionTypeService.findOne(id);
		transactionTypeService.addOptionalLabel(tt, label, summarize, summarizeTotal, synchronous);
	}

	@RequestMapping("/{id}/adduniquelabel")
	public void addUniqueLabel(
			@PathVariable Long id,
			@RequestParam("label") String label,
			@RequestParam("summarize") boolean summarize,
			@RequestParam(name = "summarizeTotal", required = false) Boolean summarizeTotal,
			@RequestParam(name = "synchronous", required = false) Boolean synchronous,
			@RequestParam("accountTypeCode") String accountTypeCode) {
		TransactionType tt = transactionTypeService.findOne(id);
		transactionTypeService.addUniqueLabel(tt, label, summarize, summarizeTotal, synchronous, accountTypeCode);
	}
	
	@RequestMapping("/{id}/addaccount")
	public void addAccount(
			@PathVariable Long id,
			@RequestParam String accountTypeCode,
			@RequestParam Boolean debit, 
			@RequestParam Boolean credit) {
		TransactionType tt = transactionTypeService.findOne(id);
		transactionTypeService.addAccount(tt, accountTypeCode, debit, credit);
	}

}
