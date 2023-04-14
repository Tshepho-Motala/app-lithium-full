package lithium.service.geo.data.projections;

import org.springframework.beans.factory.annotation.Value;

public interface CityListEntry {

	public String getCode();
	
	public String getName();
	
	@Value("#{target.name+' '+((target.level1?.name!=null)?'- '+target.level1?.name:'')+' '+((target.country?.name!=null)?'- '+target.country?.name:'')}")
	public String getCityWithCountry();
	
	@Value("#{target.country?.name}")
	public String getCountry();
	@Value("#{target.country?.code}")
	public String getCountryCode();
	@Value("#{target.country?.postalCodeFormat}")
	public String getPostalCodeFormat();
	
	@Value("#{target.level1?.name}")
	public String getLevel1();
	@Value("#{target.level1?.code}")
	public String getLevel1Code();
	
	@Value("#{target.level2?.code}")
	public String getLevel2Code();
	
	public Double getLatitude();
	
	public Double getLongitude();
	
	public Long getPopulation();
	
}
