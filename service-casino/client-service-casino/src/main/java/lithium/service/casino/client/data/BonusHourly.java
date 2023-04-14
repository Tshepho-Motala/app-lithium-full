package lithium.service.casino.client.data;

import java.io.Serializable;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(of = {"bonusCode", "available", "msTimeToAvailable", "timeAvailableStart", "timeAvailableEnd"})
public class BonusHourly implements Serializable {
	private static final long serialVersionUID = 1111752512457556368L;
	
	private Long bonusId;
	private String bonusCode;
	private String bonusName;
	private String bonusDescription;
	private byte[] image;
	private Boolean available;
	private Long msTimeToAvailable;
	private Date timeAvailableStart;
	private Date timeAvailableEnd;
}
