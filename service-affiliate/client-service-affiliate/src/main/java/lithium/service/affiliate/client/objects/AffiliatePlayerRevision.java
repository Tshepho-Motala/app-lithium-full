package lithium.service.affiliate.client.objects;

import java.io.Serializable;
import java.util.Date;

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

public class AffiliatePlayerRevision  implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;

	int version;
	
	private Affiliate affiliate;
	
	private long affiliatePlayerId;

	private Date effectiveDate;
	
	private Date archiveDate;
	
	private String primaryGuid; //usually affiliate guid (this might not be required as we have a link to affiliate in this entity)
	
	private String secondaryGuid; //usually banner guid
	
	private String tertiaryGuid;
	
	private String quaternaryGuid;
}
