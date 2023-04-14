package lithium.service.games.client.objects;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class GameDto implements Serializable {

	private String name;
	private String guid;
	private String description;
	private String cdnImageUrl;
	private GameSupplierDto gameSupplier;
}
