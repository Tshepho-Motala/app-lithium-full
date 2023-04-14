package lithium.service.pushmsg.client.objects;

import java.io.Serializable;
import java.util.List;

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
public class User implements Serializable {
	private static final long serialVersionUID = -1576192860329941132L;
	
	private String guid;
	private Domain domain;
	private List<ExternalUser> externalUsers;
	private Boolean optOut;
}