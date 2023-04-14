package lithium.service.mail.services;

import lithium.service.Response;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.user.client.UserApiInternalClient;
import lithium.service.user.client.exceptions.UserNotFoundException;
import lithium.service.user.client.objects.UserAttributesData;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lithium.service.mail.data.entities.User;
import lithium.service.mail.data.repositories.UserRepository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

import static lithium.service.user.client.objects.User.SYSTEM_FULL_NAME;
import static lithium.service.user.client.objects.User.SYSTEM_GUID;

@Service
@Slf4j
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private LithiumServiceClientFactory factory;

    @Transactional
    public User findOrCreate(String guid) {

        User user = userRepository.findByGuid(guid);

        if (user == null) {
            try {

                return buildUserFromServiceUserClient(guid);

            } catch (LithiumServiceClientFactoryException | UserNotFoundException e) {
                user = userRepository.save(User.builder().guid(guid).build());
                log.error("Can't get user from user-service for guid:" + guid + " because:" + e.getMessage() + ".Saved without being bound to a service-user user");
            }
        }
        return user;
    }

    private User buildUserFromServiceUserClient(String guid) throws UserNotFoundException, LithiumServiceClientFactoryException {

        lithium.service.user.client.objects.User user = getCompleteUserData(guid);

        if (SYSTEM_GUID.equalsIgnoreCase(guid)) {
            return userRepository.save(User.builder()
                    .guid(SYSTEM_GUID)
                    .firstName(SYSTEM_FULL_NAME)
                    .lastName("")
                    .build());
        }
        return userRepository.save(User.builder()
                .guid(user.getGuid())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .build());
    }

    private lithium.service.user.client.objects.User getCompleteUserData(String guid) throws UserNotFoundException, LithiumServiceClientFactoryException {
        Response<lithium.service.user.client.objects.User> userResponse = getClient().getUser(guid);

        if (userResponse.isSuccessful()) {
            return userResponse.getData();
        } else {
            throw new UserNotFoundException("User with guid:" + guid + " NOT FOUND");
        }
    }

    public void processUserAttributesData(UserAttributesData data) {
        User user = Optional.ofNullable(userRepository.findByGuid(data.getGuid()))
                .orElse(User.builder().guid(data.getGuid()).build());
        user.setTestAccount(data.isTestAccount());
        userRepository.save(user);
    }

    private UserApiInternalClient getClient() throws LithiumServiceClientFactoryException {
        return factory.target(UserApiInternalClient.class, true);
    }

    public void saveUser(User user) {
        userRepository.save(user);
    }

    public List<User> findUsersByFirstNameNullOrLastNameNull(Pageable page) {
        return userRepository.findUsersByFirstNameNullOrLastNameNull(page);
    }

    public User findByGuid(String guid) {
        return userRepository.findByGuid(guid);
    }

    public long countNeedToMigrateFullNamesUsers() {
        return userRepository.countByFirstNameNullOrLastNameNull();
    }

    public void updateFullNameById(long userId, String firstName, String lastname) {
        userRepository.updateFullNameById(userId, firstName, lastname);
    }
    
    public boolean isOpenUser(String guid) throws LithiumServiceClientFactoryException {
        return getCompleteUserData(guid).getStatus().getUserEnabled();
    }
}
