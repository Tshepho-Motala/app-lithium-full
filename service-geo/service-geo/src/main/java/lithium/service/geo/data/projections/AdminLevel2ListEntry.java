package lithium.service.geo.data.projections;

import org.springframework.beans.factory.annotation.Value;

public interface AdminLevel2ListEntry {
	
	public String getCode();
	
	public String getName();

	@Value("#{target.country?.code}")
	public String getLevel1Code();
	
	public Boolean getEnabled();
	
}
