package lithium.service.casino.provider.betsoft.data.request;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import lithium.service.casino.provider.betsoft.util.HashCalculator;
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
@XmlType(propOrder = {"bonusId", "hash"})
public class CancelBonusRequest extends Request {
	@XmlElement(name = "BONUSID")
	private Integer bonusId;
	
	@Override
	public String calculateHash(String password) {
		HashCalculator hashCalc = new HashCalculator(password);
		hashCalc.addItem(bonusId);
		return hashCalc.calculateHash();
	}
	
	public Map<String, String> getParamMap() {
		HashMap<String, String> map = new LinkedHashMap<String, String>();
		if (getBonusId() != null)
			map.put("bonusId", getBonusId().toString());
		if (getHash() != null)
			map.put("hash", getHash());
		return map;
	}
}