package lithium.service.casino.client.objects;

import lombok.*;

import java.io.Serializable;

@Data
@Builder
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class StartGameMock  implements Serializable {
	private static final long serialVersionUID = 1L;

	private String startGameUrl;
	private String authToken;
	private String gameProviderGuid;
	private String providerGameId;
	private String currency;
}
