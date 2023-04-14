package lithium.service.games.data.objects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString(exclude="image")
@EqualsAndHashCode
public class GameGraphicBasic {

	private long gameId;
	
	private byte[] image;
	
	private String graphicFunctionName;
	
	private boolean deleted = false;
	
	private boolean enabled = true;
	
	private String domainName; //Just for an extra check internally
}
