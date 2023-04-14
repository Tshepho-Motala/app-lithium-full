package lithium.service.casino.client.objects.roxorapi;

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
public class BetResultKind {
	private Long id;
	int version;
	private String code;
}
