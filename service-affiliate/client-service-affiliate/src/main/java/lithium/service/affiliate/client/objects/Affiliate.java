package lithium.service.affiliate.client.objects;

import java.io.Serializable;

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

public class Affiliate  implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;

	int version;

	private String userGuid;
	
	private String guid; //TODO: scenario where 2 external affiliate systems have the same guid for their affiliate identification. Unique index will fail. there are other issues with this anyway.
	
	private String additionalLinkData; // Additional identification for external affiliations. Processor impl can use this

	private Domain domain;

}
