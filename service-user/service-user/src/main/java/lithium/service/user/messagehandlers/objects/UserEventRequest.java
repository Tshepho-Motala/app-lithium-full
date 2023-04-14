package lithium.service.user.messagehandlers.objects;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown=true)
public class UserEventRequest implements Serializable {
	private static final long serialVersionUID = -7499934766590582122L;
	private String playerGuid;
	@Builder.Default
	private String room = "playerroom/userevent";
	private lithium.service.user.client.objects.UserEvent userEvent;
}
