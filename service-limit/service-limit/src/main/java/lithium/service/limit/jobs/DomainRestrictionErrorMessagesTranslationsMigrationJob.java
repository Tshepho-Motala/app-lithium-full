package lithium.service.limit.jobs;

import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.limit.data.entities.DomainRestrictionSet;
import lithium.service.limit.data.repositories.DomainRestrictionSetRepository;
import lithium.service.limit.services.RestrictionService;
import lithium.service.translate.client.objects.RestrictionError;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Service
public class DomainRestrictionErrorMessagesTranslationsMigrationJob {

    private AtomicLong updatedMessagesCount;
    private long total;

    @Autowired
    private DomainRestrictionSetRepository setRepository;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private RestrictionService service;

    @Async
    public void checkErrorMessagesTranslations(int pageSize, Long delay) throws InterruptedException {
        log.info("Error Message translations Migration job requested with: pageSize={} delay={} isMigrationJobStarted={}", pageSize, delay, JobState.isStarted);
        if (!JobState.isStarted) {
            updatedMessagesCount = new AtomicLong(0);
            total = setRepository.count();
            JobState.start();
            Pageable page = PageRequest.of(0, pageSize);
            Page<DomainRestrictionSet> restrictionSetsNeedToMigrate;
            log.info("Error Message translations check started for:" + total + " messages");

            do {

                if (JobState.isTerminated) break;
                restrictionSetsNeedToMigrate = setRepository.findAll(page);
                try {
                    migrate(restrictionSetsNeedToMigrate.toList());
                    page = page.next();
                } catch (LithiumServiceClientFactoryException e) {
                    log.error("Error Message translations  migration cannot continue because:" + e.getMessage() + ". Terminating migration job", e);
                    JobState.terminate();
                    break;
                }
                throttleMigration(delay);

            } while (restrictionSetsNeedToMigrate.hasNext());

            JobState.finish();
            log.info(":: Migration of Error Message translations  is finished." + updatedMessagesCount.get() + " Messages precessed");
        } else {
            log.info("Migration is running:" + updatedMessagesCount.get() + " of " + total + " completed");
        }
    }

    private void migrate(List<DomainRestrictionSet> usersNeedToUpdate) throws LithiumServiceClientFactoryException {

        for (DomainRestrictionSet set : usersNeedToUpdate) {
            String errorMessageKey = set.errorMessageKey();
            String translatedMessage = RestrictionError.DEFAULT_ERROR_MESSAGE.getResponseMessageLocal(messageSource,
                    set.getDomain().getName(), errorMessageKey);
            if (translatedMessage.equalsIgnoreCase(errorMessageKey)) {
                service.registerRestrictionErrorMessage(set);
                updatedMessagesCount.getAndIncrement();
            }
        }

    }

    private void throttleMigration(Long delay) throws InterruptedException {
        Thread.sleep(delay);
    }

    public void terminate() {
        JobState.terminate();
        log.info("Error Message translations MigrationJob is terminated with " + updatedMessagesCount.get() + " of " + total + " messages processed");
        updatedMessagesCount = new AtomicLong(0);
        total = 0L;
    }

    public long getUpdatedMessagesCount() {
        return updatedMessagesCount.get();
    }

    public long getTotal() {
        return total;
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
