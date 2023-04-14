package lithium.service.cashier.processor.cc.qwipi.data.enums;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum LanguageType implements Serializable {
	
	ENG("ENG"),
	RUS("RUS"),
	CHI("CHN");
	
	@Getter
	private String value;
	
}