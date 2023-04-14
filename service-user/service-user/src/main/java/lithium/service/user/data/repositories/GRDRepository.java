package lithium.service.user.data.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

import lithium.service.user.data.entities.Category;
import lithium.service.user.data.entities.GRD;
import lithium.service.user.data.entities.Group;
import lithium.service.user.data.entities.Role;

@RepositoryRestResource(collectionResourceRel = "grds", path = "grds")
public interface GRDRepository extends PagingAndSortingRepository<GRD, Long> {
//	GRD findByDomainAndGroupNameAndRoleRole(String name, String group, String role);
	GRD findByDomainNameAndGroupAndRole(String name, Group group, Role role);
	@RestResource(path = "group", rel = "group")
	List<GRD> findByGroupId(@Param("groupId") Long groupId);
	List<GRD> findByGroupIdOrderByRoleCategory(@Param("groupId") Long groupId);
	@RestResource(path = "categories", rel = "categories")
	@Query("select o.role.category from GRD o where o.group.id = :groupId group by o.role.category")
	List<Category> findCategories(@Param("groupId") Long groupId);
	@RestResource(path = "groupcat", rel = "groupcat")
  // ToDo: change to tagId
	List<GRD> findByGroupIdAndRoleCategoryId(@Param("groupId") Long groupId, @Param("tagId") Long categoryId);
	List<GRD> findByGroup(Group group);
	List<GRD> findByDomainName(String domain);
	List<GRD> findByDomainIdAndGroupId(Long domainId, Long groupId);
	List<GRD> findByDomainNameAndGroupId(String domainName, Long groupId);

  default GRD findOne(Long id) {
    return findById(id).orElse(null);
  }
}
