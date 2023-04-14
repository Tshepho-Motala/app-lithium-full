package lithium.service.limit.controllers.migration;

import lithium.leader.LeaderCandidate;
import lithium.service.Response;
import lithium.service.Response.Status;
import lithium.service.limit.jobs.RestrictionsDataMigrationJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Deprecated
@Slf4j
@RestController
@RequestMapping("/data-migration-job")
public class RestrictionsDataMigrationController {

	@Autowired
	private RestrictionsDataMigrationJob job;
	@Autowired
	private LeaderCandidate leaderCandidate;

	@GetMapping("/limit/user-restrictions")
	public Response<Void> migratePlayerRestrictions(
			@RequestParam(defaultValue = "true") boolean dryRun,
			@RequestParam(defaultValue = "10") int pageSize,
			@RequestParam(defaultValue = "1000") Long delay
	) throws Exception {
		if (!leaderCandidate.iAmTheLeader()) {
			log.debug("I am not the leader.");
			return Response.<Void>builder().status(Status.FORBIDDEN).message("I am not the leader.").build();
		}
		log.info("Migration of user restrictions is started...");
		job.migratePlayerRestrictionsData(dryRun, pageSize, delay);
		return Response.<Void>builder().status(Status.OK).message("Migration of user restrictions is started...").build();
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
