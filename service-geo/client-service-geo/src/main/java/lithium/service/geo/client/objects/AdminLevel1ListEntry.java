package lithium.service.geo.client.objects;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class AdminLevel1ListEntry {
	
	String code;
	String name;
	String countryCode;
	Boolean enabled;
}
