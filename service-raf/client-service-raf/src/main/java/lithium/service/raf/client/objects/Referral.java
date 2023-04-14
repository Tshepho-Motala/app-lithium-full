package lithium.service.raf.client.objects;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Referral {
	private Long id;
	private int version;
	private Domain domain;
	private Referrer referrer;
	private Date timestamp;
	private Boolean converted;
	private String playerGuid;
}
