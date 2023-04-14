package lithium.service.user.jobs;

import java.util.List;
import lithium.service.user.client.objects.UserAttributesData;
import lithium.service.user.data.entities.User;
import lithium.service.user.data.entities.UserCategory;
import lithium.service.user.services.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@AllArgsConstructor
public class DataMigrationService {

  @Autowired
  private UserService userService;

  @Transactional(readOnly = true)
  public List<UserAttributesData> getUserAttributesForSync(Pageable page) {
    Page<User> users = (Page<User>) userService.findAll((Specification<User>) page);
    return users.stream()
        .map(user -> UserAttributesData.builder()
            .guid(user.guid())
            .testAccount(user.getTestAccount())
            .createdDate(user.getCreatedDate())
            .statusId(user.getStatus().getId())
            .playerTagIds(getPlayerTagIds(user))
            .build())
        .toList();
  }

  private List<Long> getPlayerTagIds(User user) {
    return user.getUserCategories()
        .stream().map(UserCategory::getId)
        .toList();
  }
}
