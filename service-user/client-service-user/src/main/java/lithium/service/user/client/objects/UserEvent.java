package lithium.service.user.client.objects;

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
@ToString
public class UserEvent implements Serializable {
	private static final long serialVersionUID = 99114915855296484L;
	
	private Long id;
	private User user;
	private String websocketSessionId;
	private String type;
	private String message;
	private String data;
	private Boolean received;
}