package lithium.service.affiliate.provider;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@AllArgsConstructor(access=AccessLevel.PRIVATE)
public enum AffiliateTranType {
	AFFILIATE_DEBIT ("AFFILIATE_DEBIT"),
	AFFILIATE_CREDIT ("AFFILIATE_CREDIT"),
	AFFILIATE_BALANCE("AFFILIATE_BALANCE");
	
	@Setter
	@Getter
	@Accessors(fluent = true)
	private String value;
	
	public String toString() {
		return value;
	}
}
