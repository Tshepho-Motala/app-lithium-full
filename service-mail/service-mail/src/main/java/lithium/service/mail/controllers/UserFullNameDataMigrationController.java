package lithium.service.mail.controllers;

import lithium.leader.LeaderCandidate;
import lithium.service.Response;
import lithium.service.Response.Status;
import lithium.service.mail.jobs.UserFullNameMigrationJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

//@Deprecated
@Slf4j
@RestController
@RequestMapping("/data-migration-job")
public class UserFullNameDataMigrationController {

    @Autowired
    private UserFullNameMigrationJob job;
    @Autowired
    private LeaderCandidate leaderCandidate;

    @GetMapping("/service-mail/users-full-names")
    public Response<Void> migrateUserFullNames(
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "1000") Long delay
    ) throws Exception {
        if (!leaderCandidate.iAmTheLeader()) {
            log.debug("I am not the leader.");
            return Response.<Void>builder().status(Status.FORBIDDEN).message("I am not the leader.").build();
        }
        job.migrateUsersFullName(pageSize, delay);
        if (job.getTotal() > 0){
            String logMessage = "Migration job is running. Last state: " + job.getUpdatedUsersCount()+ " of " + job.getTotal() + " users processed";
            return Response.<Void>builder().status(Status.OK).message(logMessage).build();
        }
        return Response.<Void>builder().status(Status.OK).message("Migration of users full names is started.").build();
    }

    @GetMapping("/terminate")
    public Response<Void> terminate() {
        if (!leaderCandidate.iAmTheLeader()) {
            log.debug("I am not the leader.");
            return Response.<Void>builder().status(Status.FORBIDDEN).message("I am not the leader.").build();
        }
        job.terminate();
        return Response.<Void>builder().status(Status.OK).message("Job terminated...").build();
    }
}
