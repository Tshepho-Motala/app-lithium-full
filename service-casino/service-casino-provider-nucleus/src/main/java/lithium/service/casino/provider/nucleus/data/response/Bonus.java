package lithium.service.casino.provider.nucleus.data.response;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "BONUS")
public class Bonus {
	@XmlElement(name = "BONUSID")
	private Integer bonusId;
	@XmlElement(name = "AWARDEDDATE")
	private String awardedDate;
	@XmlElement(name = "ROUNDS")
	private Integer rounds;
	@XmlElement(name = "ROUNDSLEFT")
	private Integer roundsLeft;
	@XmlElement(name = "GAMEIDS")
	private String gameIds;
	@XmlElement(name = "DESCRIPTION")
	private String description;
	@XmlElement(name = "STARTTIME")
	private String startTime;
	@XmlElement(name = "EXPIRATIONTIME")
	private String expirationTime;
	@XmlElement(name = "DURATION")
	private Integer duration;
}