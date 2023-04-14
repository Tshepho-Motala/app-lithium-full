package lithium.service.accountinghistory.controllers.backoffice;

import lithium.service.accounting.objects.TransactionEntryBO;
import lithium.service.accountinghistory.service.AccountingService;
import lithium.service.client.datatable.DataTableRequest;
import lithium.service.client.datatable.DataTableResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@RestController
@RequestMapping("/backoffice/balance-movement")
public class BalanceMovementController {

    @Autowired
    private AccountingService service;

    @RequestMapping(value = "/list")
    public  DataTableResponse<TransactionEntryBO> list(
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
            @RequestParam(value = "pageSize", required = false, defaultValue = "50") Integer pageSize
    ) throws Exception {

        if (orderDirection == null) {
            orderDirection = "desc";
        }
        return service.backofficeBalanceMovementTransactionsClient().list(dateRangeStart, dateRangeEnd, userGuid, transactionType, orderDirection, providerTransId, searchValue, domainName, roundId, page, pageSize);
    }

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
    ) throws Exception {
        List<String> domains = new ArrayList<>();
        domains.add(domainName);
        log.debug("Transactions table requested [domains=" + domains.stream().collect(Collectors.joining(","))
                + ", dateRangeStart=" + dateRangeStart + ", dateRangeEnd=" + dateRangeStart + ", userGuid=" + userGuid
                + ", draw=" + draw + ", start=" + start + ", length=" + length + ", search=" + searchValue
                + ", providerTransId=" + providerTransId + ", transactionType=" + transactionType + "]");

        if (length > 100) length = 100;

        if (orderDirection == null) {
            orderDirection = "desc";
        }

        PageRequest pageRequest = PageRequest.of(start / length, length, Sort.Direction.fromString(orderDirection), new String[]{"id"});
        DataTableRequest request = new DataTableRequest();
        request.setPageRequest(pageRequest);

        DataTableResponse<TransactionEntryBO> table = service.backofficeBalanceMovementTransactionsClient().table(dateRangeStart, dateRangeEnd, userGuid, draw, start, length, transactionType, orderDirection, providerTransId, searchValue, domainName, roundId);
        return table;
    }

    @RequestMapping(value = "/xls")
    public void xls(
            @RequestParam(name = "dateRangeStart", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") String dateRangeStart,
            @RequestParam(name = "dateRangeEnd", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") String dateRangeEnd,
            @RequestParam(name = "userGuid") String userGuid,
            @RequestParam(name = "length") int length,
            @RequestParam(name = "transactionType", required = false) List<String> transactionType,
            @RequestParam(name = "providerTransId", required = false) String providerTransId,
            @RequestParam(name = "domainName") String domainName,
            HttpServletResponse response
    ) throws Exception {
        String name = "Balance_Movement_" + userGuid.replaceAll("/", "_");
        String fileName = name + ".xlsx";
        String mimeType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
        response.setContentType(mimeType);
        response.setHeader("Content-Disposition", String.format("attachment; filename=\"" + fileName +"\""));
        response.setHeader("x-filename", String.format(fileName));

        DataTableResponse<TransactionEntryBO> table = service.backofficeBalanceMovementTransactionsClient().table(dateRangeStart, dateRangeEnd, userGuid, "1", 0, length, transactionType, null, providerTransId, null, domainName, null);
        service.xls(table.getData(), response.getOutputStream());
    }
}
