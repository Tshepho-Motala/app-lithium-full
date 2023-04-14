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
public class AffiliatePlayer  implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Long id;
	
	int version;
	
	private String playerGuid;
	
	private AffiliatePlayerRevision current;

}
