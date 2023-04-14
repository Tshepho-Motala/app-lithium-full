package lithium.service.games.client.objects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude={"image"})
public class GameGraphic {
	long id;
	String name;
	byte[] image;
	boolean enabled;
	String gameGuid;
	String url;
	String graphicFunctionName;
}
