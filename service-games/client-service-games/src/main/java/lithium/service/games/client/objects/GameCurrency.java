package lithium.service.games.client.objects;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GameCurrency implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String currencyCode;
	private Long minimumAmountCents;
}
