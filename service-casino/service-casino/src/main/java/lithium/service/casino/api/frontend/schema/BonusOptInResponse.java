package lithium.service.casino.api.frontend.schema;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class BonusOptInResponse implements Serializable {
	private static final long serialVersionUID = -1L;

	private Boolean bonusOptIn;
	private String message;
}