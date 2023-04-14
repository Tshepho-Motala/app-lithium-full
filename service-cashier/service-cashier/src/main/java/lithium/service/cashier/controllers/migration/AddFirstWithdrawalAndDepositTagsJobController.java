package lithium.service.cashier.controllers.migration;


import lithium.leader.LeaderCandidate;
import lithium.service.Response;
import lithium.service.cashier.jobs.migration.AddFirstWithdrawalAndDepositTagsJob;
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
@RequestMapping("/data-migration-job/add-first-success-transactions-tags")
public class AddFirstWithdrawalAndDepositTagsJobController {

    @Autowired
    private LeaderCandidate leaderCandidate;

    @Autowired
    private AddFirstWithdrawalAndDepositTagsJob job;

    @GetMapping("/start")
    public Response<Void> processFixTransactionsAmount(
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "1000") Long delay,
            @RequestParam(defaultValue = "0") int startPage
    ) throws Exception {

        if (!leaderCandidate.iAmTheLeader()) {
            log.debug("I am not the leader.");
            return Response.<Void>builder().status(Response.Status.FORBIDDEN).message("I am not the leader.").build();
        }
        job.startJob(pageSize, delay, startPage);
        if (job.getUpdatedUsersCount() > 0){
            String logMessage = "Update job is running. Last state: " + job.getUpdatedUsersCount()+ " of " + job.getTotal() + " users processed";
            return Response.<Void>builder().status(Response.Status.OK).message(logMessage).build();
        }
        return Response.<Void>builder().status(Response.Status.OK).message("Update of transactions first deposit & withdrawal tags is started.").build();
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
