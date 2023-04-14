package lithium.service.user.data.repositories;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lithium.service.user.data.entities.Domain;
import lithium.service.user.data.entities.User;
import lithium.service.user.data.entities.UserLink;
import lithium.service.user.data.entities.UserLinkType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface UserLinkRepository extends PagingAndSortingRepository<UserLink, Long> {

  ArrayList<UserLink> findByPrimaryUserAndDeletedFalse(User user);
  ArrayList<UserLink> findByPrimaryUserAndDeletedFalseAndUserLinkType(User user, UserLinkType userLinkType);
  //LSPLAT-6028 The single returns is because we using ID guid strategy
  UserLink findByPrimaryUserAndDeletedFalseAndUserLinkTypeCode(User user, String code);
  UserLink findByPrimaryUserAndSecondaryUserAndDeletedFalseAndUserLinkType(User primaryUser, User secondaryUser, UserLinkType userLinkType);
  ArrayList<UserLink> findBySecondaryUserAndDeletedFalse(User user);
  Optional<UserLink> findByPrimaryUserAndSecondaryUserAndUserLinkTypeCode(User primaryUser, User secondaryUser, String code);

  Page<UserLink> findByPrimaryUserDomainInAndDeletedFalse(List<Domain> domainList, Pageable pageable);

  Optional<UserLink> findOneByPrimaryUserAndSecondaryUserAndDeletedFalseAndUserLinkTypeCode(User primaryUser, User secondaryUser, String code);

  default UserLink findOne(Long id) {
    return findById(id).orElse(null);
  }
}
