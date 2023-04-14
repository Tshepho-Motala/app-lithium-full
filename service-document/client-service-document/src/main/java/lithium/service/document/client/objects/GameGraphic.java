package lithium.service.document.client.objects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GameGraphic {
	long id;
	String name;
	byte[] image;
	boolean enabled;
	String gameGuid;
	String graphicFunctionName;
}
