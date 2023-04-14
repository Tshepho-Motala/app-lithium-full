package lithium.service.casino.provider.nucleus.data.request;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import lithium.service.casino.provider.nucleus.util.HashCalculator;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {
			"userId", "bankId", "rounds", "games", "comment", "description", "extBonusId", "startTime", "expirationTime",
			"duration", "expirationHours", "frbTableRoundChips", "hash"
		})
public class AwardBonusRequest extends Request {
	@XmlElement(name="USERID")
	private String userId;
	@XmlElement(name="BANKID")
	private String bankId;
	@XmlElement(name="ROUNDS")
	private Integer rounds;
	@XmlElement(name="GAMES")
	private String games;
	@XmlElement(name="COMMENT")
	private String comment;
	@XmlElement(name="DESCRIPTION")
	private String description;
	@XmlElement(name="EXTBONUSID")
	private String extBonusId;
	@XmlElement(name="STARTTIME")
	private String startTime;
	@XmlElement(name="EXPIRATIONTIME")
	private String expirationTime;
	@XmlElement(name="DURATION")
	private Integer duration;
	@XmlElement(name="EXPIRATIONHOURS")
	private Integer expirationHours;
	@XmlElement(name="FRBTABLEROUNDCHIPS")
	private Integer frbTableRoundChips;
	
	@Override
	public String calculateHash(String password) {
		HashCalculator hashCalc = new HashCalculator(password);
		hashCalc.addItem(userId);
		hashCalc.addItem(bankId);
		hashCalc.addItem(rounds);
		hashCalc.addItem(games);
		hashCalc.addItem(comment);
		hashCalc.addItem(description);
		hashCalc.addItem(extBonusId);
		return hashCalc.calculateHash();
	}
	
	public Map<String, String> getParamMap() {
		HashMap<String, String> map = new LinkedHashMap<String, String>();
		if (getUserId() != null)
			map.put("userId", getUserId());
		if (getBankId() != null)
			map.put("bankId", getBankId());
		if (getRounds() != null)
			map.put("rounds", getRounds().toString());
		if (getGames() != null)
			map.put("games", getGames());
		if (getComment() != null)
			map.put("comment", getComment());
		if (getDescription() != null)
			map.put("description", getDescription());
		if (getExtBonusId() != null)
			map.put("extBonusId", getExtBonusId());
		if (getStartTime() != null)
			map.put("startTime", getStartTime());
		if (getExpirationTime() != null)
			map.put("expirationTime", getExpirationTime());
		if (getDuration() != null)
			map.put("duration", getDuration().toString());
		if (getExpirationHours() != null)
			map.put("expirationHours", getExpirationHours().toString());
		if (getFrbTableRoundChips() != null)
			map.put("frbTableRoundChips", getFrbTableRoundChips().toString());
		if (getHash() != null)
			map.put("hash", getHash());
		return map;
	}
}