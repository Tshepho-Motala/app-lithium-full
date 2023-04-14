package lithium.service.user.client.objects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class RestrictionData implements Serializable {
	private static final long serialVersionUID = -1;
	private Long domainRestrictionId;
	private String domainRestrictionName;
	private String guid;
	private String domainName;
	private boolean enabled ;
	private boolean deleted ;
	private Date activeFrom;
	private Date activeTo;
	private RestrictionsMessageType messageType;
	private Integer subType;
}
