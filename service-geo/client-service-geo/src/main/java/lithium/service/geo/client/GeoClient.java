package lithium.service.geo.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lithium.service.Response;
import lithium.service.geo.client.objects.AdminLevel1ListEntry;
import lithium.service.geo.client.objects.AdminLevel2ListEntry;
import lithium.service.geo.client.objects.CityListEntry;
import lithium.service.geo.client.objects.Country;
import lithium.service.geo.client.objects.CountryListEntry;
import lithium.service.geo.client.objects.Location;

@FeignClient(name="service-geo")
public interface GeoClient {
	
	@RequestMapping("/geo/countries")
	public Response<List<CountryListEntry>> countries();
	
	@RequestMapping("/geo/countries/{countryCode}/level1")
	public Response<Iterable<AdminLevel1ListEntry>> level1(@PathVariable("countryCode") String countryCode); 
	
	@RequestMapping("/geo/countries/{countryCode}/level1/{level1Code}/level2")
	public Response<Iterable<AdminLevel2ListEntry>> level2(@PathVariable("level1Code") String level1Code); 
	
	@RequestMapping("/geo/countries/{countryCode}/level1/{level1Code}/cities")
	public Response<Iterable<CityListEntry>> level1Cities(@PathVariable("level1Code") String level1Code); 

	@RequestMapping("/geo/countries/{countryCode}/cities")
	public Response<Iterable<CityListEntry>> countryCities(@PathVariable("countryCode") String countryCode); 

	@RequestMapping("/geo/countries/{countryCode}/level1/{level1Code}/level2/{level2Code}/cities")
	public Response<Iterable<CityListEntry>> level2Cities(@PathVariable("level2Code") String level2Code); 

	@RequestMapping("/geo/locationv4")
	public Response<Location> location(@RequestParam("ipv4") String ipv4);

	@RequestMapping("/geo/countries/code/{countryCode}")
	public Response<Country> countryByCode(@PathVariable("countryCode") String countryCode);

	@RequestMapping("/geo/countries/ISO/{ISOCode}")
	public Response<Country> countryByIso(@PathVariable("ISOCode") String ISOCode);


}
