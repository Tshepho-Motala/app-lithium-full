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
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude={"image"})
public class Graphic implements Serializable {

	@Serial
	private static final long serialVersionUID = -4436132483754411210L;
	private Long id;
	private int version;
	private byte[] image;
	private String name;
	private Long size;
	private String type;
}
