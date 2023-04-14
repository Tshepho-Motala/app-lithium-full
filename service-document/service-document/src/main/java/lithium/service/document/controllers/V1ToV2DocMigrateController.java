package lithium.service.document.controllers;

import lithium.leader.LeaderCandidate;
import lithium.service.Response;
import lithium.service.Response.Status;
import lithium.service.document.jobs.V1ToV2DocumentMigrationJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

//@Deprecated
@Slf4j
@RestController
@RequestMapping("/data-migration-job/documents/migrate")
public class V1ToV2DocMigrateController {

    @Autowired
    private V1ToV2DocumentMigrationJob job;
    @Autowired
    private LeaderCandidate leaderCandidate;

    @GetMapping("/v1-to-v2")
    public Response<Void> migrateUserFullNames(
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "1000") Long delay
    ) throws Exception {
        if (!leaderCandidate.iAmTheLeader()) {
            log.debug("I am not the leader.");
            return Response.<Void>builder().status(Status.FORBIDDEN).message("I am not the leader.").build();
        }
        job.migrateDocumentsData(pageSize, delay);
        if (job.getTotal() > 0){
            String logMessage = "Migration job is running. Last state: " + job.getMigratedDocsCount()+ " of " + job.getTotal() + " docs processed," + job.getV2CreatedDocCount();
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
        job.stopJobAndResetCounters("terminated");
        return Response.<Void>builder().status(Status.OK).message("Job terminated...").build();
    }
}
