package lithium.service.document.controllers;

import lithium.leader.LeaderCandidate;
import lithium.service.Response;
import lithium.service.document.jobs.DocumentsDataMigrationJob;
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
public class DocumentsDataMigrationController {

	@Autowired
	private DocumentsDataMigrationJob job;
	@Autowired
	private LeaderCandidate leaderCandidate;

	@GetMapping("/document/user-documents")
	public Response<Void> migratePlayerDocuments(
			@RequestParam(defaultValue = "true") boolean dryRun,
			@RequestParam(defaultValue = "10") int pageSize,
			@RequestParam(defaultValue = "1000") Long delay
	) throws Exception {
		if (!leaderCandidate.iAmTheLeader()) {
			log.debug("I am not the leader.");
			return Response.<Void>builder().status(Response.Status.FORBIDDEN).message("I am not the leader.").build();
		}
		log.info("Migration of user documents is started...");
		job.migratePlayerDocumentsData(dryRun, pageSize, delay);
		return Response.<Void>builder().status(Response.Status.OK).message("Migration of user documents is started...").build();
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
