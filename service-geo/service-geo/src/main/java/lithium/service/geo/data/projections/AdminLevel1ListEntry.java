package lithium.service.geo.data.projections;

import org.springframework.beans.factory.annotation.Value;

public interface AdminLevel1ListEntry {
	
	public String getCode();
	
	public String getName();
	
	@Value("#{target.country?.name}")
	public String getCountry();
	@Value("#{target.country?.code}")
	public String getCountryCode();
	@Value("#{target.country?.postalCodeFormat}")
	public String getPostalCodeFormat();
	
	@Value("#{target.name+' - '+target.country?.name}")
	public String getAdminLevel1WithCountry();
	
	public Boolean getEnabled();
}
