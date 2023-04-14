package lithium.service.mail.jobs;

import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.mail.services.UserService;
import lithium.service.user.client.UserApiInternalClient;
import lithium.service.user.client.exceptions.UserNotFoundException;
import lithium.service.user.client.objects.User;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import static java.util.Optional.ofNullable;
import static lithium.util.StringUtil.isNumeric;

@Slf4j
@Service
public class UserFullNameMigrationJob {

    private static final String NOT_SPECIFIED = "not specified";
    private static final String EMPTY_STRING = "";
    public static final String DEFAULT_ADMIN_GUID = "default/admin";
    private AtomicLong updatedUsersCount;
    private long total;

    @Autowired
    private LithiumServiceClientFactory lithiumServiceClientFactory;
    @Autowired
    private UserService userService;


    @Async
    public void migrateUsersFullName(int pageSize, Long delay) throws InterruptedException {
        log.info("Full Names Migration job requested with: pageSize={} delay={} isMigrationJobStarted={}", pageSize, delay, JobState.isStarted);
        if (!JobState.isStarted) {
            updatedUsersCount = new AtomicLong(0);
            total = userService.countNeedToMigrateFullNamesUsers();
            JobState.start();
            Pageable page = PageRequest.of(0, pageSize);
            List<lithium.service.mail.data.entities.User> usersNeedToUpdate = userService.findUsersByFirstNameNullOrLastNameNull(page);
            log.info("Users full names migration started for:" + total + " users");

            do {

                if (JobState.isTerminated) break;
                try {
                    migrate(usersNeedToUpdate);
                } catch (LithiumServiceClientFactoryException e) {
                    log.error("User full names for mail-service migration cannot continue because:" + e.getMessage() + ". Terminating migration job", e);
                    JobState.terminate();
                    break;
                }
                throttleMigration(delay);
                usersNeedToUpdate = userService.findUsersByFirstNameNullOrLastNameNull(page);

            } while (usersNeedToUpdate.size() > 0);

            JobState.finish();
            log.info(":: Migration of users full names is finished." + updatedUsersCount.get() + " Users precessed");
        } else {
            log.info("Migration is running:" + updatedUsersCount.get() + " of " + total + " completed");
        }
    }

    private void migrate(List<lithium.service.mail.data.entities.User> usersNeedToUpdate) throws LithiumServiceClientFactoryException {

        for (lithium.service.mail.data.entities.User localUser : usersNeedToUpdate) {

            // Since it does not exist in the svc-user but presented on envs on svc-mail. Check and update it "manually"
            if (User.SYSTEM_GUID.equalsIgnoreCase(localUser.getGuid()) || DEFAULT_ADMIN_GUID.equalsIgnoreCase(localUser.getGuid())) {
                updateSystemUser(localUser.getId());
                log.debug("userId:" + localUser.getId() + "fullName updated to system");
            } else {
                try {
                    // if the guid strategy at first was USERNAME, then it became ID, or backward, we can get duplicated users inside the svc-mail.user.
                    // both of them links to the same user in the svc-user.user but have a different guid.
                    User externalUser = getExternalUserOrThrow(localUser.getGuid());
                    if (localUser.getGuid().equalsIgnoreCase(externalUser.getGuid())) {
                        updateUserFullName(localUser, externalUser);
                    } else {
                        checkAndUpdateUser(localUser, externalUser);
                    }
                } catch (UserNotFoundException e) {
                    updateAbnormalUser(localUser);
                    log.error(e.getMessage() + " Updated whit empty full name");
                }
            }
        }
        log.info("Successfully migrated full names for " + updatedUsersCount.get() + "of" + total + " users");
    }

    private User getExternalUserOrThrow(String guid) throws UserNotFoundException, LithiumServiceClientFactoryException {
        User externalUser = getUserApiInternalClient().getUser(guid).getData();

        if (externalUser == null) {
            externalUser = getExternalUserById(guid);
        }

        if (externalUser != null) {
            return externalUser;
        } else {
            throw new UserNotFoundException("svc-mail.user DB contains user with guid:" + guid + ".Which not presented in svc-user");
        }
    }

    private User getExternalUserById(String guid) throws UserNotFoundException, LithiumServiceClientFactoryException {
        String usernameOrId = getUserIdentificationData(guid);
        if (!isNumeric(usernameOrId)) {
            throw new UserNotFoundException("svc-mail.user DB contains user with incorrect guid:" + guid);
        }
        long expectedUserId = Long.parseLong(usernameOrId);
        return getUserApiInternalClient().getUserById(expectedUserId).getData();
    }

    private void checkAndUpdateUser(lithium.service.mail.data.entities.User localUser, User externalUser) {

        String usernameOrId = getUserIdentificationData(localUser.getGuid());

        lithium.service.mail.data.entities.User expectedDuplicateUser = getExpectedDuplicateUser(externalUser, usernameOrId);

        if (expectedDuplicateUser != null && expectedDuplicateUser.getId() != localUser.getId()) {
            log.warn("User FullName migration job found a duplicate for userid:" + localUser.getId() + ".Duplicate userId:" + expectedDuplicateUser.getId());
            updateUserFullName(expectedDuplicateUser, externalUser);
        }
        updateUserFullName(localUser, externalUser);
    }

    private String getUserIdentificationData(String guid) {
        String[] split = guid.split("/", 2);
        if (split.length != 2) {
            return null;
        } else {
            return split[1];
        }
    }

    private lithium.service.mail.data.entities.User getExpectedDuplicateUser(User externalUser, String usernameOrId) {
        lithium.service.mail.data.entities.User expectedDuplicateUser = userService.findByGuid(externalUser.getGuid());
        if (expectedDuplicateUser == null && isNumeric(usernameOrId)) {
            String expectedGuidForUsernameStrategy = externalUser.getDomain().getName() + "/" + externalUser.getUsername();
            expectedDuplicateUser = userService.findByGuid(expectedGuidForUsernameStrategy);
        }
        return expectedDuplicateUser;
    }

    private void updateSystemUser(long userId) {
        userService.updateFullNameById(userId, User.SYSTEM_FULL_NAME, EMPTY_STRING);
        updatedUsersCount.getAndIncrement();
    }

    private void updateAbnormalUser(lithium.service.mail.data.entities.User localUser) {
        if (localUser.getFirstName() == null) {
            localUser.setFirstName(EMPTY_STRING);
        }
        if (localUser.getLastName() == null) {
            localUser.setLastName(EMPTY_STRING);
        }
        userService.updateFullNameById(localUser.getId(), localUser.getFirstName(), localUser.getLastName());
        updatedUsersCount.getAndIncrement();
    }

    private void updateUserFullName(lithium.service.mail.data.entities.User localUser, User externalUser) {

        log.debug("User whit guid:" + localUser.getGuid() + " will be updated[" +
                "FirstName from:" + localUser.getFirstName() + " to:" + externalUser.getFirstName() + "." +
                "LastName from:" + localUser.getLastName() + " to:" + externalUser.getLastName() + "]");

        String firstName = ofNullable(externalUser.getFirstName()).orElse(NOT_SPECIFIED);
        String lastName = ofNullable(externalUser.getLastName()).orElse(NOT_SPECIFIED);
        userService.updateFullNameById(localUser.getId(), firstName, lastName);
        updatedUsersCount.getAndIncrement();
    }

    private void throttleMigration(Long delay) throws InterruptedException {
        Thread.sleep(delay);
    }

    public void terminate() {
        JobState.terminate();
        log.info("FullNames MigrationJob is terminated with " + updatedUsersCount.get() + " of " + total + " users processed");
        updatedUsersCount = new AtomicLong(0);
        total = 0L;
    }

    private UserApiInternalClient getUserApiInternalClient() throws LithiumServiceClientFactoryException {
        UserApiInternalClient client = lithiumServiceClientFactory.target(UserApiInternalClient.class,
                "service-user", true);
        return client;
    }

    public long getUpdatedUsersCount() {
        return updatedUsersCount.get();
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
