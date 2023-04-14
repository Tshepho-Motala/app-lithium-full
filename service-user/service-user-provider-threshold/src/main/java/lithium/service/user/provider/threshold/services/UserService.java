package lithium.service.user.provider.threshold.services;

import lithium.exceptions.Status500InternalServerErrorException;
import lithium.service.user.provider.threshold.data.entities.User;

public interface UserService extends AbstractService<User> {

   User updateUser(User user, boolean isTestAccount) throws Status500InternalServerErrorException;

   User findByGuid(String userGuid);

   User findOrCreate(String userGuid) throws Status500InternalServerErrorException;
}

