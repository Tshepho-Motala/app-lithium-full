package lithium.service.translate.client.objects;

import lombok.Data;

@Data
public class Language {

	private long id;
	private String locale3;
	private String locale2;
	private String description;
	private boolean enabled;
	
}
