package lithium.service.geo.client.objects;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class CityListEntry {
	
	String code;
	String name;
	String countryCode;
	String level1Code;
	String level2Code;
	Double latitude;
	Double longitude;
	Long population;
	
}
