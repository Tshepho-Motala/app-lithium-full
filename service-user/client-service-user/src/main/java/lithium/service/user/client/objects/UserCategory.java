package lithium.service.user.client.objects;

import java.io.Serial;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

@Data
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class UserCategory implements Serializable {

	@Serial
	private static final long serialVersionUID = 1736649191068221010L;
	private Long id;
	private String name;
	private String description;
}
