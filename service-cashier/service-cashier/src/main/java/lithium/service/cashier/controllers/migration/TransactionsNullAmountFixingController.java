package lithium.service.cashier.controllers.migration;

import lithium.leader.LeaderCandidate;
import lithium.service.Response;
import lithium.service.cashier.jobs.migration.TransactionsNullAmountFixingJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

//TODO: Deprecate after using
//@Deprecated

@Slf4j
@RestController
@RequestMapping("/data-migration-job")
public class TransactionsNullAmountFixingController {

    @Autowired
    private LeaderCandidate leaderCandidate;
    @Autowired
    private TransactionsNullAmountFixingJob job;

    @GetMapping("/transaction-amount-fix")
    public Response<Void> processFixTransactionsAmount(
        @RequestParam(defaultValue = "10") int pageSize,
        @RequestParam(defaultValue = "1000") Long delay
    ) throws Exception {

        if (!leaderCandidate.iAmTheLeader()) {
            log.debug("I am not the leader.");
            return Response.<Void>builder().status(Response.Status.FORBIDDEN).message("I am not the leader.").build();
        }
            job.migrateTransactionsAmountFix(pageSize, delay);
            if (job.getTotal() > 0){
                String logMessage = "Migration job is running. Last state: " + job.getUpdatedTransactionsCount()+ " of " + job.getTotal() + " transactions processed";
                return Response.<Void>builder().status(Response.Status.OK).message(logMessage).build();
            }
            return Response.<Void>builder().status(Response.Status.OK).message("Migration of transactions amount is started.").build();
        }

        @GetMapping("/terminate")
        public Response<Void> terminate() {
            if (!leaderCandidate.iAmTheLeader()) {
                log.debug("I am not the leader.");
                return Response.<Void>builder().status(Response.Status.FORBIDDEN).message("I am not the leader.").build();
            }
            job.terminate();
            return Response.<Void>builder().status(Response.Status.OK).message("Job terminated...").build();
        }
}
