package lithium.service.changelog.controllers.backoffice;

import lithium.leader.LeaderCandidate;
import lithium.service.Response;
import lithium.service.changelog.jobs.VerificationSubCategoryMigrationJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

// TODO: Deprecate after migration
//@Deprecated

@Slf4j
@RestController
@RequestMapping("/data-migration-job")
public class SubCategoryUpdateController {

    @Autowired
    private VerificationSubCategoryMigrationJob job;

    @Autowired
    private LeaderCandidate leaderCandidate;

    @GetMapping("/update-subcategory")
    public Response<Void> processUpdateSubCategory(
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "1000") Long delay
    ) throws Exception {

        if (!leaderCandidate.iAmTheLeader()) {
            log.debug("I am not the leader.");
            return Response.<Void>builder().status(Response.Status.FORBIDDEN).message("I am not the leader.").build();
        }
        job.migrateUpdateSubCategory(pageSize, delay);
        if (job.getTotal() > 0){
            String logMessage = "Migration job is running. Last state: " + job.getUpdatedChangeLogsCount()+ " of " + job.getTotal() + " change logs processed";
            return Response.<Void>builder().status(Response.Status.OK).message(logMessage).build();
        }
        return Response.<Void>builder().status(Response.Status.OK).message("Migration of change logs is started.").build();
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
