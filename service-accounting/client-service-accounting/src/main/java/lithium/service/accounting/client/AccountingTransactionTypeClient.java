package lithium.service.accounting.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lithium.service.Response;
import lithium.service.accounting.objects.TransactionType;

@FeignClient(name="service-accounting", path="/transactiontype")
public interface AccountingTransactionTypeClient {

	@RequestMapping("/find") 
	public Response<TransactionType> findByCode(@RequestParam("code") String code);
	
	@RequestMapping("/create")
	public Response<TransactionType> create(@RequestParam("code") String code);
		
	@RequestMapping("/{id}/addlabel")
	public void addLabel(@PathVariable("id") Long id, 
			@RequestParam("label") String label, 
			@RequestParam("summarize") boolean summarize,
            @RequestParam("summarizeTotal") Boolean summarizeTotal,
            @RequestParam("synchronous") Boolean synchronous);
	
	@RequestMapping("/{id}/adduniquelabel")
	public void addUniqueLabel(@PathVariable("id") Long id, 
			@RequestParam("label") String label, 
			@RequestParam("summarize") boolean summarize,
            @RequestParam("summarizeTotal") Boolean summarizeTotal,
            @RequestParam("synchronous") Boolean synchronous,
			@RequestParam("accountTypeCode") String accountTypeCode);
	
	@RequestMapping("/{id}/addoptionallabel")
	public void addOptionalLabel(@PathVariable("id") Long id, 
			@RequestParam("label") String label, 
			@RequestParam("summarize") boolean summarize,
	        @RequestParam("summarizeTotal") Boolean summarizeTotal,
	        @RequestParam("synchronous") Boolean synchronous);
	
	@RequestMapping("/{id}/addaccount")
	public void addAccount(@PathVariable("id") Long id, @RequestParam("accountTypeCode") String accountTypeCode,
			@RequestParam("debit") Boolean debit, 
			@RequestParam("credit") Boolean credit);

}
