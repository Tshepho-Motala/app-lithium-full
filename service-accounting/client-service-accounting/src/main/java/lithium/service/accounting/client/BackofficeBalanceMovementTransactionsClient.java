package lithium.service.accounting.client;

import lithium.service.Response;
import lithium.service.accounting.objects.TransactionEntryBO;
import lithium.service.accounting.objects.TransactionType;
import lithium.service.client.datatable.DataTableResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name="service-accounting", path="/backoffice/balance-movement")
public interface BackofficeBalanceMovementTransactionsClient {

    @RequestMapping("/table")
    public DataTableResponse<TransactionEntryBO> table(
            @RequestParam(name = "dateRangeStart") @DateTimeFormat(pattern = "yyyy-MM-dd") String dateRangeStart,
            @RequestParam(name = "dateRangeEnd") @DateTimeFormat(pattern = "yyyy-MM-dd") String dateRangeEnd,
            @RequestParam(name = "userGuid") String userGuid,
            @RequestParam(name = "draw") String draw,
            @RequestParam(name = "start") int start,
            @RequestParam(name = "length") int length,
            @RequestParam(name = "transactionType") List<String> transactionType,
            @RequestParam(name = "order[0][dir]", required = false) String orderDirection,
            @RequestParam(name = "providerTransId", required=false) String providerTransId,
            @RequestParam(name = "search", required=false) String searchValue,
            @RequestParam(name = "domainName") String domainName,
            @RequestParam(name = "roundId", required = false) String roundId
    ) throws Exception;

    @RequestMapping("/types")
    Response<List<TransactionType>> getUserTransactionTypes(
            @RequestParam("userGuid") String userGuid
    ) throws Exception;

    @RequestMapping("/list")
    DataTableResponse<TransactionEntryBO> list(
            @RequestParam(name = "dateRangeStart", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") String dateRangeStart,
            @RequestParam(name = "dateRangeEnd", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") String dateRangeEnd,
            @RequestParam(name = "userGuid") String userGuid,
            @RequestParam(name = "transactionType", required = false) List<String> transactionType,
            @RequestParam(name = "order[0][dir]", required = false) String orderDirection,
            @RequestParam(name = "providerTransId", required = false) String providerTransId,
            @RequestParam(name = "search", required = false) String searchValue,
            @RequestParam(name = "domainName") String domainName,
            @RequestParam(name = "roundId", required = false) String roundId,
            @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
            @RequestParam(value = "pageSize", required = false, defaultValue = "50") Integer pageSize ) throws Exception ;


}
