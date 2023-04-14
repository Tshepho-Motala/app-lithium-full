package lithium.service.user.search.data.repositories.user;

import lithium.service.user.data.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository("user.UserRepository")
public interface UserRepository extends PagingAndSortingRepository<User, Long>, JpaSpecificationExecutor<User> {

  User findByDomainNameAndUsername(String domain, String username);

  default Page<User> findAllBy(Specification<User> spec, Pageable p) {
    if (p.getSort() == null) {
      // Default sort order
      return findAll(spec, PageRequest.of(p.getPageNumber(), p.getPageSize(), Sort.Direction.DESC, "id"));
    }

    Order order = p.getSort().iterator().next();
    String property = order.getProperty();
    Direction direction = order.getDirection();

    if (property.equalsIgnoreCase("id")) {
      property = "id";
    } else if (property.equalsIgnoreCase("username")) {
      property = "username";
    } else if (property.equalsIgnoreCase("status.name")) {
      property = "status.name";
    } else if (property.equalsIgnoreCase("statusReason.description")) {
      property = "statusReason.description";
    } else if (property.equalsIgnoreCase("domain.name")) {
      property = "domain.name";
    } else if (property.equalsIgnoreCase("firstName")) {
      property = "firstName";
    } else if (property.equalsIgnoreCase("lastName")) {
      property = "lastName";
    } else if (property.equalsIgnoreCase("email")) {
      property = "email";
    } else if (property.equalsIgnoreCase("cellphoneNumber")) {
      property = "cellphoneNumber";
    } else if (property.equalsIgnoreCase("residentialAddress.postalCode")) {
      property = "residentialAddress.postalCode";
    } else if (property.equalsIgnoreCase("lastLoggedInDate")) {
      property = "lastLogin.date";
    } else if (property.equalsIgnoreCase("providerAuthClient")) {
      property = "lastLogin.providerAuthClient";
    } else if (property.equalsIgnoreCase("loggedOutDate")) {
      property = "lastLogin.loggedOutDate";
    } else if (property.equalsIgnoreCase("duration")) {
      property = "lastLogin.duration";
    } else if (property.equalsIgnoreCase("currentLoggedInDate")) {
      property = "session.date";
    } else if (property.equalsIgnoreCase("userApiToken.shortGuid")) {
      property = "userApiToken.shortGuid";
    } else if (property.equalsIgnoreCase("userCategories")) {
      property = "userCategories";
    } else if (property.equalsIgnoreCase("gender")) {
      property = "gender";
    } else if (property.equalsIgnoreCase("createdDate")) {
      property = "createdDate";
    }

    return findAll(spec, PageRequest.of(p.getPageNumber(), p.getPageSize(), direction, property));
  }
}
