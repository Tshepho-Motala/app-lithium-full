package lithium.service.geo.controllers;

import javax.servlet.http.HttpServletRequest;

import lithium.service.geo.data.entities.Country;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lithium.service.Response;
import lithium.service.geo.data.projections.AdminLevel1ListEntry;
import lithium.service.geo.data.projections.AdminLevel2ListEntry;
import lithium.service.geo.data.projections.CityListEntry;
import lithium.service.geo.data.projections.CountryListEntry;
import lithium.service.geo.data.repositories.AdminLevel1Repository;
import lithium.service.geo.data.repositories.AdminLevel2Repository;
import lithium.service.geo.data.repositories.CityRepository;
import lithium.service.geo.data.repositories.CountryRepository;
import lithium.service.geo.objects.Location;
import lithium.service.geo.services.MaxMindLookupService;
import lithium.util.IPUtil;

@RestController
@RequestMapping
public class GeoController {

	@Autowired CountryRepository countryRepository;
	@Autowired AdminLevel1Repository adminLevel1Repository;
	@Autowired AdminLevel2Repository adminLevel2Repository;
	@Autowired CityRepository cityRepository;
	@Autowired MaxMindLookupService maxMindLookupService;
	
	@RequestMapping("/geo/countries")
	public Response<Iterable<CountryListEntry>> countries() {
		return Response.<Iterable<CountryListEntry>>builder().data(countryRepository.findByIso3NotNullOrderByName()).build();
	}
	
	@RequestMapping("/geo/countries/{country}")
	public Response<Iterable<CountryListEntry>> countries(
		@PathVariable("country") String country
	) {
		return Response.<Iterable<CountryListEntry>>builder().data(
			countryRepository.findByIso3NotNullAndNameStartingWithOrCodeStartingWithOrContinentStartingWithOrderByName(country, country, country)
		).build();
	}
	
	@RequestMapping("/geo/countries/{countryCode}/level1")
	public Response<Iterable<AdminLevel1ListEntry>> countryLevel1s(@PathVariable("countryCode") String countryCode) { 
		return Response.<Iterable<AdminLevel1ListEntry>>builder().data(adminLevel1Repository.findByCountryCodeOrderByName(countryCode)).build();
	}
	
	@RequestMapping("/geo/countries/{countryCode}/level1/{level1}")
	public Response<Iterable<AdminLevel1ListEntry>> countryLevel1s(
		@PathVariable("countryCode") String countryCode,
		@PathVariable("level1") String level1
	) { 
		return Response.<Iterable<AdminLevel1ListEntry>>builder().data(adminLevel1Repository.findByCountryCodeAndNameStartingWithOrderByName(countryCode, level1)).build();
	}
	
	@RequestMapping("/geo/level1s/{level1}")
	public Response<Iterable<AdminLevel1ListEntry>> level1s(@PathVariable("level1") String level1) { 
		return Response.<Iterable<AdminLevel1ListEntry>>builder().data(adminLevel1Repository.findTop50ByNameStartingWithOrderByName(level1)).build();
	}
	
	@RequestMapping("/geo/countries/{countryCode}/level1/{level1Code}/level2")
	public Response<Iterable<AdminLevel2ListEntry>> level2(@PathVariable("level1Code") String level1Code) { 
		return Response.<Iterable<AdminLevel2ListEntry>>builder().data(adminLevel2Repository.findByLevel1Code(level1Code)).build();
	}
	
	@RequestMapping("/geo/countries/{countryCode}/level1/{level1Code}/cities")
	public Response<Iterable<CityListEntry>> level1Cities(@PathVariable("level1Code") String level1Code) { 
		return Response.<Iterable<CityListEntry>>builder().data(cityRepository.findByLevel1Code(level1Code)).build();
	}
	
	@RequestMapping("/geo/countries/{countryCode}/level1/{level1Code}/cities/{city}")
	public Response<Iterable<CityListEntry>> level1CitiesSearch(
		@PathVariable("level1Code") String level1Code,
		@PathVariable("city") String city
	) { 
		return Response.<Iterable<CityListEntry>>builder().data(cityRepository.findTop200ByLevel1CodeAndNameStartingWithOrderByName(level1Code, city)).build();
	}
	
	@RequestMapping("/geo/countries/{countryCode}/cities")
	public Response<Iterable<CityListEntry>> countryCities(@PathVariable("countryCode") String countryCode) { 
		return Response.<Iterable<CityListEntry>>builder().data(cityRepository.findByCountryCode(countryCode)).build();
	}
	
	@RequestMapping("/geo/countries/{countryCode}/cities/{city}")
	public Response<Iterable<CityListEntry>> countryCities(
		@PathVariable("countryCode") String countryCode,
		@PathVariable("city") String city
	) { 
		return Response.<Iterable<CityListEntry>>builder().data(cityRepository.findTop200ByCountryCodeAndNameStartingWithOrderByName(countryCode, city)).build();
	}
	
	@RequestMapping("/geo/cities/{city}")
	public Response<Iterable<CityListEntry>> cities(@PathVariable("city") String city) { 
		return Response.<Iterable<CityListEntry>>builder().data(cityRepository.findTop200ByNameStartingWithOrderByName(city)).build();
	}
	
	@RequestMapping("/geo/countries/{countryCode}/level1/{level1Code}/level2/{level2Code}/cities")
	public Response<Iterable<CityListEntry>> level2Cities(@PathVariable("level2Code") String level2Code) { 
		return Response.<Iterable<CityListEntry>>builder().data(cityRepository.findByLevel2Code(level2Code)).build();
	}

	@RequestMapping("/geo/locationv4")
	public Response<Location> location(@RequestParam("ipv4") String ipv4) {
		return Response.<Location>builder().data(maxMindLookupService.lookup(ipv4)).build();
	}
	
	@RequestMapping("/geo/locationFromRequest")
	public Response<Location> locationByIp(HttpServletRequest request) {
		 return Response.<Location>builder().data2(maxMindLookupService.lookup(IPUtil.ipFromRequest(request))).build();
	}

	@RequestMapping("/geo/countries/code/{countryCode}")
	public Response<Country> countryByCountryCode(@PathVariable("countryCode") String countryCode) {
		return Response.<Country>builder().data(countryRepository.findByCode(countryCode)).build();
	}
	@RequestMapping("/geo/countries/ISO/{ISOCode}")
	public Response<Country> countryByCountryIso(@PathVariable("ISOCode") String ISOCode) {
		return Response.<Country>builder().data(countryRepository.findByIso3(ISOCode)).build();
	}

}
