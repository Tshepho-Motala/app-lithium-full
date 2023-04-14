package lithium.service.geo.data.repositories;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.geo.data.entities.Country;
import lithium.service.geo.data.projections.AdminLevel1ListEntry;
import lithium.service.geo.data.entities.AdminLevel1;

public interface AdminLevel1Repository extends PagingAndSortingRepository<AdminLevel1, Long> {

	AdminLevel1 findByCode(String code);
	List<AdminLevel1ListEntry> findByCountry(Country country);
	List<AdminLevel1ListEntry> findByCountryCode(String code);
	List<AdminLevel1ListEntry> findByCountryCodeOrderByName(String code);
	
	// We would normally add the IgnoreCase pattern here, but since mysql without binary colation is case insensitive,
	// adding it just adds a lot of mysql processing because the left side is uppercased limiting the use of indexes.
	List<AdminLevel1ListEntry> findByCountryCodeAndNameStartingWithOrderByName(String countryCode, String name);
	List<AdminLevel1ListEntry> findTop50ByNameStartingWithOrderByName(String name);

}
