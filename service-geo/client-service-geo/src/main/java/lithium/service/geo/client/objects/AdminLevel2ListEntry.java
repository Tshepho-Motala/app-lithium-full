package lithium.service.geo.client.objects;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class AdminLevel2ListEntry {
	
	String code;
	String name;
	String level1Code;
	Boolean enabled;
	
}
