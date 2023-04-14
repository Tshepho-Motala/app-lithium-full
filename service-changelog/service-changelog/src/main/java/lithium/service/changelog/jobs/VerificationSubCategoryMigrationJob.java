package lithium.service.changelog.jobs;

import lithium.client.changelog.Category;
import lithium.client.changelog.SubCategory;
import lithium.service.changelog.data.entities.ChangeLog;
import lithium.service.changelog.data.repositories.ChangeLogFieldChangeRepository;
import lithium.service.changelog.data.repositories.ChangeLogRepository;
import lithium.service.changelog.data.repositories.SubCategoryRepository;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Service
public class VerificationSubCategoryMigrationJob {

    private AtomicLong updatedChangeLogsCount;
    private long total;
    @Autowired
    private SubCategoryRepository subCategoryRepository;

    @Autowired
    private ChangeLogRepository changeLogRepository;

    @Autowired
    private ChangeLogFieldChangeRepository changeLogFieldChangeRepository;

    public void migrateUpdateSubCategory(int pageSize, Long delay) throws InterruptedException {
        log.info("UpdateSubCategory Migration job requested with: pageSize={} delay={} isMigrationJobStarted={}", pageSize, delay, JobState.isStarted);
        if (!JobState.isStarted) {
            updatedChangeLogsCount = new AtomicLong(0);
            JobState.start();
            int i = 0;
            Pageable page = PageRequest.of(i, pageSize);
            List<ChangeLog> changeLogsNeedToUpdate = changeLogRepository.findByCategoryNameAndSubCategoryName(Category.ACCOUNT.getName(), SubCategory.EDIT_DETAILS.getName(), page);
            total = changeLogsNeedToUpdate.size();
            log.info("UpdateSubCategory Migration started for:" + total + " changeLogs");

            do {
                if (JobState.isTerminated) break;
                try {
                    migrate(changeLogsNeedToUpdate);
                } catch (Exception e) {
                    log.error("UpdateSubCategory Migration job cannot continue because:" + e.getMessage() + ". Terminating migration job", e);
                    JobState.terminate();
                    break;
                }
                throttleMigration(delay);
                page = PageRequest.of(i++, pageSize);
                changeLogsNeedToUpdate = changeLogRepository.findByCategoryNameAndSubCategoryName(Category.ACCOUNT.getName(), SubCategory.EDIT_DETAILS.getName(), page);

            } while (changeLogsNeedToUpdate.size() > 0);

            JobState.finish();
            log.info(":: UpdateSubCategory Migration job is finished." + updatedChangeLogsCount.get() + " change logs precessed");
        } else {
            log.info("Migration is running:" + updatedChangeLogsCount.get() + " of " + total + " completed");
        }
    }

    private void migrate(List<ChangeLog> changeLogsNeedToUpdate) {
        lithium.service.changelog.data.entities.SubCategory kycCategory = subCategoryRepository.findByName(SubCategory.KYC.getName());

        changeLogsNeedToUpdate.stream()
                .filter(changeLog -> changeLogFieldChangeRepository.findByChangeLog(changeLog).stream()
                        .anyMatch(changeLogFieldChange -> changeLogFieldChange.getField().equalsIgnoreCase("verification_status")))
                .forEach(changeLog -> {
                    changeLog.setSubCategory(kycCategory);
                    changeLogRepository.save(changeLog);
                    log.info("Fixed subcategory for changelog id=" + changeLog.getId());
                    updatedChangeLogsCount.getAndIncrement();
                });
    }

    public long getTotal() {
        return total;
    }

    public long getUpdatedChangeLogsCount() {
        return updatedChangeLogsCount.get();
    }

    private void throttleMigration(Long delay) throws InterruptedException {
        Thread.sleep(delay);
    }

    public void terminate() {
        JobState.terminate();
        log.info("VerificationSubCategory MigrationJob is terminated with " + updatedChangeLogsCount.get() + " of " + total + " change logs processed");
        updatedChangeLogsCount = new AtomicLong(0);
        total = 0L;
    }

    @Getter
    private static class JobState {
        private static boolean isStarted;
        private static boolean isTerminated;

        public static void start() {
            isStarted = true;
            isTerminated = false;
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
