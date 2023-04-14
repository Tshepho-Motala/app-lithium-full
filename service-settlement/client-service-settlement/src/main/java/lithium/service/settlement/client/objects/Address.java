package lithium.service.settlement.client.objects;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class Address implements Serializable {
	private static final long serialVersionUID = -8586012009095938132L;
	
	private String addressLine1;

	private String addressLine2;
	
	private String addressLine3;
	
	private String city;
	
	private String cityCode;
	
	private String adminLevel1;
	
	private String adminLevel1Code;
	
	private String country;
	
	private String countryCode;
	
	private String postalCode;
	
	public String getOneLinerFull() {
		return toOneLinerFull();
	}
	
	public static void appendWithComma(StringBuffer sb, String value) {
		if (value == null) return;
		if (value.isEmpty()) return;
		if (sb.length() > 0) sb.append(", ");
		sb.append(value);
	}
	
	public String toOneLinerStreet() {
		StringBuffer result = new StringBuffer(); 
		Address.appendWithComma(result, addressLine1);
		Address.appendWithComma(result, addressLine2);
		Address.appendWithComma(result, addressLine3);
		return result.toString();
	}
	
	public String toOneLinerFull() {
		StringBuffer result = new StringBuffer(); 
		Address.appendWithComma(result, addressLine1);
		Address.appendWithComma(result, addressLine2);
		Address.appendWithComma(result, addressLine3);
		Address.appendWithComma(result, city);
		Address.appendWithComma(result, adminLevel1);
		Address.appendWithComma(result, country);
		Address.appendWithComma(result, postalCode);
		return result.toString();
	}
}
