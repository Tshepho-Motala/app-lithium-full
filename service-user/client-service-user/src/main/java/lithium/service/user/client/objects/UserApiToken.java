package lithium.service.user.client.objects;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserApiToken implements Serializable {
	private static final long serialVersionUID = 1L;

	private long id;
	
	private String token;
	
	private String guid;

	private String shortGuid;
}
