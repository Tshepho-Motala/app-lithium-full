package lithium.service.casino.client.data;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BonusDisplayMinimal implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private Long bonusId;
	private String bonusCode;
	private String bonusName;
	private String bonusDescription;
	private byte[] image;
}
