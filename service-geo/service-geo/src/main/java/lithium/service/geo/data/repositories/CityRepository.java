package lithium.service.geo.data.repositories;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.geo.data.entities.AdminLevel1;
import lithium.service.geo.data.entities.AdminLevel2;
import lithium.service.geo.data.entities.City;
import lithium.service.geo.data.entities.Country;
import lithium.service.geo.data.projections.AdminLevel1ListEntry;
import lithium.service.geo.data.projections.CityListEntry;


public interface CityRepository extends PagingAndSortingRepository<City, Long> {

	City findByCode(String code);
	List<City> findByCountryCodeAndName(String countryCode, String name);
	List<CityListEntry> findByLevel1(AdminLevel1 level1);
	List<CityListEntry> findByLevel1Code(String level1Code);
	List<CityListEntry> findByLevel2(AdminLevel2 level2);
	List<CityListEntry> findByLevel2Code(String level2Code);
	List<CityListEntry> findByCountry(Country country);
	List<CityListEntry> findByCountryCode(String countryCode);
	// We would normally add the IgnoreCase pattern here, but since mysql without binary colation is case insensitive,
	// adding it just adds a lot of mysql processing because the left side is uppercased limiting the use of indexes.
	List<CityListEntry> findTop200ByCountryCodeAndNameStartingWithOrderByName(String countryCode, String city);
	List<CityListEntry> findTop200ByLevel1CodeAndNameStartingWithOrderByName(String level1, String city);
	List<CityListEntry> findTop200ByNameStartingWithOrderByName(String city);

}
