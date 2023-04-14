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
@ToString(exclude={"base64"})
public class GraphicBasic implements Serializable {

	@Serial
	private static final long serialVersionUID = 1608336067981826463L;
	private String filetype;
	private String filename;
	private long filesize;
	private byte[] base64;
}
