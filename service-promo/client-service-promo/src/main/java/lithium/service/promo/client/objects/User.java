package lithium.service.promo.client.objects;

import java.io.Serial;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class User implements Serializable {

	@Serial
	private static final long serialVersionUID = -8401787534403685342L;
	private Long id;
	private int version;
	private String guid;
}
