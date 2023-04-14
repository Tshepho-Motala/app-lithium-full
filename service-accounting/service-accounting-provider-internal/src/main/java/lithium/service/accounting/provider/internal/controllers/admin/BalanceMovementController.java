package lithium.service.accounting.provider.internal.controllers.admin;

import lithium.exceptions.Status425DateParseException;
import lithium.service.Response;
import lithium.service.accounting.client.BackofficeBalanceMovementTransactionsClient;

import lithium.service.accounting.objects.TransactionEntryBO;
import lithium.service.accounting.objects.TransactionType;
import lithium.service.accounting.provider.internal.services.SummaryAccountTransactionTypeService;
import lithium.service.accounting.provider.internal.services.TransactionEntryService;
import lithium.service.client.datatable.DataTableRequest;
import lithium.service.client.datatable.DataTableResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@RestController
@RequestMapping("/backoffice/balance-movement")
public class BalanceMovementController implements BackofficeBalanceMovementTransactionsClient {

    @Autowired
    private TransactionEntryService service;

    @Autowired
    private SummaryAccountTransactionTypeService summaryAccountTransactionTypeService;

    @Override
    @RequestMapping("/list")
    public DataTableResponse<TransactionEntryBO> list(
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
            @RequestParam(value = "pageSize", required = false, defaultValue = "50") Integer pageSize ) throws Exception {

            List<String> domains = new ArrayList<>();
            domains.add(domainName);

            DataTableRequest request = new DataTableRequest();
            PageRequest pageRequest = PageRequest.of(page, pageSize, Sort.Direction.fromString(orderDirection), new String[]{"id"});
            request.setPageRequest(pageRequest);

            Page<TransactionEntryBO> table = service.findBalanceMovementTransactions(domains, dateRangeStart, dateRangeEnd, userGuid, null,
                searchValue, null, providerTransId, transactionType, null, roundId, pageRequest);

            DataTableResponse<TransactionEntryBO> dtResponse = new DataTableResponse<>(request, table);
            dtResponse.setCurrentPage(page);

            Double ceil = Math.ceil(dtResponse.getRecordsTotal()/pageSize);

            if (pageSize * ceil < dtResponse.getRecordsTotal()) ceil ++;

            dtResponse.setRecordsTotalPages(ceil.intValue());

            return dtResponse;
    }

    @Override
    @RequestMapping(value = "/table")
    public DataTableResponse<TransactionEntryBO> table(
            @RequestParam(name = "dateRangeStart", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") String dateRangeStart,
            @RequestParam(name = "dateRangeEnd", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") String dateRangeEnd,
            @RequestParam(name = "userGuid") String userGuid,
            @RequestParam(name = "draw") String draw,
            @RequestParam(name = "start") int start,
            @RequestParam(name = "length") int length,
            @RequestParam(name = "transactionType", required = false) List<String> transactionType,
            @RequestParam(name = "order[0][dir]", required = false) String orderDirection,
            @RequestParam(name = "providerTransId", required = false) String providerTransId,
            @RequestParam(name = "search", required = false) String searchValue,
            @RequestParam(name = "domainName") String domainName,
            @RequestParam(name = "roundId", required = false) String roundId
    ) throws Status425DateParseException {
        List<String> domains = new ArrayList<>();
        domains.add(domainName);
        log.debug("Transactions table requested [domains=" + domains.stream().collect(Collectors.joining(","))
                + ", dateRangeStart=" + dateRangeStart + ", dateRangeEnd=" + dateRangeStart + ", userGuid=" + userGuid
                + ", draw=" + draw + ", start=" + start + ", length=" + length + ", search=" + searchValue
                + ", providerTransId=" + providerTransId + ", roundId=" + roundId + ", transactionType=" + transactionType + "]");

        if (orderDirection == null) {
            orderDirection = "desc";
        }

        PageRequest pageRequest = PageRequest.of(start / length, length, Sort.Direction.fromString(orderDirection), new String[]{"id"});
        DataTableRequest request = new DataTableRequest();
        request.setPageRequest(pageRequest);

        Page<TransactionEntryBO> table = service.findBalanceMovementTransactions(domains, dateRangeStart, dateRangeEnd, userGuid, null,
                searchValue, null, providerTransId, transactionType, null, roundId, pageRequest);
        return new DataTableResponse<>(request, table);
    }

    @Override
    @GetMapping("/types")
    public Response<List<TransactionType>> getUserTransactionTypes(
            @RequestParam("userGuid") String userGuid) {
        List<TransactionType> types = summaryAccountTransactionTypeService.findUserBalanceMovementTypes(userGuid);
        return Response.<List<TransactionType>>builder().data(types).status(Response.Status.OK).build();
    }
}
