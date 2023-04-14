package lithium.service.accounting.client;

import java.util.ArrayList;
import java.util.List;

import lithium.service.accounting.objects.TransactionLabelBasic;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lithium.service.accounting.objects.LabelValue;
import lithium.service.accounting.objects.TransactionLabelContainer;

@FeignClient("service-accounting")
public interface AccountingTransactionLabelClient {
	@RequestMapping("/transaction/addLabels")
	public void addLabels(@RequestBody TransactionLabelContainer labelContainer) throws Exception;
	@RequestMapping("/transaction/findLabelsForTransaction")
	public List<LabelValue> findLabelsForTransaction(@RequestParam("tranId") Long tranId) throws Exception;
	@RequestMapping("/transaction/find-labels-by-external-transaction")
	public TransactionLabelContainer findLabelsByExternalTransaction(@RequestParam("exTranId") String externalTransactionId, @RequestParam("typeCode")  String transactionTypeCode);
}
