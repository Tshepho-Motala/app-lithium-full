package lithium.service.affiliate.client.objects;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class AffiliatePlayerBasic  implements Serializable {

	private static final long serialVersionUID = 1L;

	private String playerGuid;
	
	private String primaryGuid;
	
	private String secondaryGuid;
	
	private String tertiaryGuid;
	
	private String quaternaryGuid;
}
