package lithium.service.cashier.controllers;

import lithium.service.Response;
import lithium.service.cashier.client.objects.TransactionType;
import lithium.service.cashier.services.SummaryTransactionTypesService;
import lithium.service.cashier.services.TransactionService;
import lithium.service.client.objects.Granularity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Optional;

@RestController
@RequestMapping("/player-transaction-statistics")
@Slf4j
public class PlayerTransactionStatisticsController {

    @Autowired
    private TransactionService transactionService;
    @Autowired
    private SummaryTransactionTypesService summaryTransactionTypesService;

    @GetMapping("/deposits/count")
    public Response<Long> getDepositsCount(@RequestParam("domainName") String domainName, @RequestParam("userGuid") String userGuid, @RequestParam("status") Optional<String> status) {
        try {
            Long depositCount = transactionService.getCountByUserAndTransactionTypeAndStatusCode(userGuid, TransactionType.DEPOSIT, status);
            return Response.<Long>builder().data(depositCount).status(Response.Status.OK).build();
        } catch (IllegalArgumentException ex) {
            log.error("Got error during execute '/deposits/count'. Params: 'domainName'={}, 'userGuid'={}, 'status'={}",domainName, userGuid, status.get(), ex);
            return Response.<Long>builder().status(Response.Status.BAD_REQUEST).build();
        }
    }
    @GetMapping("/summary-transaction-types/bets")
    public Response<Long> getSummaryTransactionTypesBets(@RequestParam("domainName") String domainName, @RequestParam("userGuid") String userGuid)  {
        try {
            Long betsSummary = summaryTransactionTypesService.getAccountSummaryByTransactionTypes(
                    domainName,
                    userGuid,
                    Granularity.GRANULARITY_TOTAL.granularity(),
                    "PLAYER_BALANCE",
                    Arrays.asList("CASINO_BET", "SPORTS_BET")
            );
            return Response.<Long>builder().data(betsSummary).status(Response.Status.OK).build();
        } catch (Exception ex) {
            log.error("Got error during execute '/summary-transaction-types/bets'. Params: 'domainName'={}, 'userGuid'={}",domainName, userGuid, ex);
            return Response.<Long>builder().status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }
}
