package lithium.service.access.services.duplicatecheck;

import lithium.service.access.client.exceptions.Status551ServiceAccessClientException;
import lithium.service.access.client.objects.UserDuplicatesTypes;
import lithium.service.user.client.objects.User;
import java.util.List;


public interface UserDuplicateProcessor {

  List<User> findDuplicates(User user) throws Status551ServiceAccessClientException;

  UserDuplicatesTypes getType();


}
