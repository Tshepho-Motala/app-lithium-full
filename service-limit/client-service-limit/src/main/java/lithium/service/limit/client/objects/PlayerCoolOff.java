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
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class PlayerCoolOff implements Serializable {
	private static final long serialVersionUID = -5052505767900589031L;

	private Long id;
	private int version;
	private String playerGuid;
	private Date createdDate;
	private Integer periodInDays;
	private Date expiryDate;
	private String createdDateDisplay;
	private String expiryDateDisplay;
}
