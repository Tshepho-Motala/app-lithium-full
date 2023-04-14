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
public class Promotion implements Serializable {

	@Serial
	private static final long serialVersionUID = -3737213088413504269L;
	private Long id;
	private int version;
	private User editor;
	private PromotionRevision current;
	private PromotionRevision edit;
}
