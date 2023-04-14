package lithium.service.geo.data.repositories;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.geo.data.entities.AdminLevel1;
import lithium.service.geo.data.entities.AdminLevel2;
import lithium.service.geo.data.projections.AdminLevel2ListEntry;

public interface AdminLevel2Repository extends PagingAndSortingRepository<AdminLevel2, Long> {

	AdminLevel2 findByCode(String code);
	List<AdminLevel2ListEntry> findByLevel1(AdminLevel1 adminLevel1);
	List<AdminLevel2ListEntry> findByLevel1Code(String adminLevel1Code);

}
