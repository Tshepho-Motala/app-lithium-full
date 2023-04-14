package lithium.service.document.jobs;

import lithium.service.document.client.stream.UserDocumentsTriggerStream;
import lithium.service.document.data.entities.DocumentV2;
import lithium.service.document.data.repositories.DocumentV2Repository;
import lithium.service.user.client.objects.UserDocumentData;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class DocumentsDataMigrationJob {

	@Autowired
	private DocumentV2Repository documentV2Repository;

	@Autowired
	private UserDocumentsTriggerStream documentsTriggerStream;

	@Async
	public void migratePlayerDocumentsData(boolean dryRun, int pageSize, Long delay) throws InterruptedException {
		log.info("service-document/user-documents, isMigrationJobStarted={}", JobState.isStarted);
		if (!JobState.isStarted) {
			JobState.start(dryRun);
			Pageable page = PageRequest.of(0, pageSize);
			long migratedAccountsCount;
			do {
				if (JobState.isTerminated) break;
				migratedAccountsCount = migrate(page);
				page = page.next();
				throttleMigration(delay);
			} while (migratedAccountsCount > 0);
			JobState.finish();
			log.info(":: Migration of users documents is finished.");
		}
	}

	private long migrate(Pageable page) {
		return fromExternalStorage(page).stream().peek(this::toLocalStorage).count();
	}

	private List<UserDocumentData> fromExternalStorage(Pageable page) {
		List<DocumentV2> usersDocuments = documentV2Repository.findAllByDeletedFalse(page);
		List<UserDocumentData> userDocumentData = new ArrayList<>();
		usersDocuments.stream().forEach(document -> {
			userDocumentData.add(UserDocumentData.builder()
					.documentId(document.getId())
					.guid(document.getOwner().getGuid())
					.statusId(document.getReviewStatus().getId())
					.statusName(document.getReviewStatus().getName())
					.sensitive(document.isSensitive())
					.deleted(document.isDeleted())
					.build());
		});
		return userDocumentData;
	}

	private void toLocalStorage(UserDocumentData data) {
		try {
			if (!JobState.isDryRun) documentsTriggerStream.trigger(data);
			log.info("Migrated document : {}", data);
		} catch (Exception e) {
			log.error("Got error during documents migration (" + data + ")" + "\n:: Migration of this document is rolled back...", e);
		}
	}

	private void throttleMigration(Long delay) throws InterruptedException {
		Thread.sleep(delay);
	}

	public void terminate() {
		JobState.terminate();
	}

	@Getter
	private static class JobState {

		private static boolean isStarted;
		private static boolean isTerminated;
		private static boolean isDryRun;

		public static void start(boolean dryRun) {
			isStarted = true;
			isTerminated = false;
			isDryRun = dryRun;
		}

		public static void terminate() {
			isTerminated = true;
			isStarted = false;
		}

		private static void finish() {
			isStarted = false;
		}
	}
}
