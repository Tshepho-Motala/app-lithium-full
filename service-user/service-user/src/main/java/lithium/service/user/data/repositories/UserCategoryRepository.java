package lithium.service.user.data.repositories;

import java.util.List;
import java.util.Set;
import lithium.service.user.data.entities.Domain;
import lithium.service.user.data.entities.User;
import lithium.service.user.data.entities.UserCategory;
import lithium.service.user.data.entities.UserCategoryProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface UserCategoryRepository extends PagingAndSortingRepository<UserCategory, Long>, JpaSpecificationExecutor<UserCategory> {
  UserCategoryProjection findByNameAndDomain(String name, Domain domain);
  UserCategoryProjection findUserCategoriesById(Long id);
  List<UserCategory> findAllByUsersIn(List<User> users);
  List<UserCategoryProjection> findAllByIdInAndDomainName(List<Long> ids, String domainName);
  List<UserCategory> findByIdInAndDomainName(List<Long> ids, String domainName);
  Set<UserCategoryProjection> findAllByDomainIn(List<Domain> domain);
  Page<UserCategoryProjection> findByDomainIn(List<Domain> domain, Pageable pageable);
  Page<UserCategoryProjection> findByDomainInAndNameContainingOrDescriptionContaining(List<Domain> domain, String name, String description, Pageable pageable);
  Set<UserCategoryProjection> findAllByIdIn(List<Long> ids);
  List<UserCategory> findAllByDomainName(String domainName);
}
