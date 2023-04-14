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
public class Domain implements Serializable {
	@Serial
	private static final long serialVersionUID = 201094285684037102L;
	private Long id;
	private int version;
	private String name;
}
