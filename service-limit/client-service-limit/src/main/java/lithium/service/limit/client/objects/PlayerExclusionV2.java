package lithium.service.limit.client.objects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

@Data
@Builder
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class PlayerExclusionV2 implements Serializable {
	private static final long serialVersionUID = -1;

	private Long id;
	private int version;
	private String playerGuid;
	private Date createdDate;
	private String createdDateDisplay;
	private Date expiryDate;
	private String expiryDateDisplay;
	private boolean permanent;
	private String message;
	private String advisor;
	private ExclusionSource exclusionSource;
}
