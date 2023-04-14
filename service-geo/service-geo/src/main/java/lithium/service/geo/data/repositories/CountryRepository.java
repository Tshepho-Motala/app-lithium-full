package lithium.service.geo.data.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.geo.data.entities.Country;
import lithium.service.geo.data.projections.CountryListEntry;

public interface CountryRepository extends PagingAndSortingRepository<Country, Long> {
	
	Country findByCode(String code);
	Country findByIso3(String iso3);
	Country findByIsoNr(Integer isoNr);
	Country findByFips(String fips);
	
	Iterable<CountryListEntry> findByIso3NotNullOrderByName();
	Iterable<CountryListEntry> findByIso3NotNullAndNameStartingWithOrCodeStartingWithOrContinentStartingWithOrderByName(String name, String countryCode, String continent);
}
