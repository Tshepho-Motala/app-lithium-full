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
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@ToString(callSuper=true)
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"extBonusId", "bankId", "hash"})
public class CheckBonusRequest extends Request {
	@XmlElement(name = "EXTBONUSID")
	private String extBonusId;
	@XmlElement(name = "BANKID")
	private String bankId;
	
	@Override
	public String calculateHash(String password) {
		HashCalculator hashCalc = new HashCalculator(password);
		hashCalc.addItem(extBonusId);
		hashCalc.addItem(bankId);
		return hashCalc.calculateHash();
	}
	
	public Map<String, String> getParamMap() {
		HashMap<String, String> map = new LinkedHashMap<String, String>();
		if (getExtBonusId() != null)
			map.put("extBonusId", getExtBonusId());
		if (getBankId() != null)
			map.put("bankId", getBankId());
		if (getHash() != null)
			map.put("hash", getHash());
		return map;
	}
}