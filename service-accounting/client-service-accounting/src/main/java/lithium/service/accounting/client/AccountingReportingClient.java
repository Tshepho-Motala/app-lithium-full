package lithium.service.accounting.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lithium.service.client.datatable.DataTableResponse;

@FeignClient("service-accounting")
public interface AccountingReportingClient {

	@RequestMapping("transaction/findPlayerTransactionsForDateRangeAndUserGuid")
	public DataTableResponse<lithium.service.accounting.objects.TransactionEntry> findPlayerTransactionsForDateRangeAndUserGuid(
			@RequestParam("startDate") String startDate, 
			@RequestParam("endDate") String endDate, 
			@RequestParam("userGuid") String userGuid,
			@RequestParam("draw") String draw,
			@RequestParam("start") String start,
			@RequestParam("length") String length) throws Exception;
	
	@RequestMapping("transaction/findLabelsForTransaction")
	public List<lithium.service.accounting.objects.LabelValue> findLabelsForTransaction(
			@RequestParam("tranId") Long tranId) throws Exception;
}
