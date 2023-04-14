package lithium.service.cashier.processor.neteller.data.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@ToString
@JsonFormat(shape=JsonFormat.Shape.OBJECT)
@AllArgsConstructor(access=AccessLevel.PRIVATE)
public enum Locale {
	LANGUAGE_DA_DK("da_DK", "Danish"),
	LANGUAGE_DE_DE("de_DE", "German"),
	LANGUAGE_EL_GR("el_GR", "Greek"),
	LANGUAGE_EN_US("en_US", "English"),
	LANGUAGE_ES_ES("es_ES", "Spanish"),
	LANGUAGE_FR_FR("fr_FR", "French"),
	LANGUAGE_IT_IT("it_IT", "Italian"),
	LANGUAGE_JA_JP("ja_JP", "Japanese"),
	LANGUAGE_KO_KR("ko_KR", "Korean"),
	LANGUAGE_NO_NO("no_NO", "Norwegian"),
	LANGUAGE_PL_PL("pl_PL", "Polish"),
	LANGUAGE_PT_PT("pt_PT", "Portuguese"),
	LANGUAGE_RU_RU("ru_RU", "Russian"),
	LANGUAGE_SV_SE("sv_SE", "Swedish"),
	LANGUAGE_TR_TR("tr_TR", "Turkish"),
	LANGUAGE_ZH_CN("zh_CN", "Simplified Chinese");
	
	@Getter
	@Setter
	@Accessors(fluent=true)
	private String code;
	@Getter
	@Setter
	@Accessors(fluent=true)
	private String description;
	
	@JsonCreator
	public static Locale fromCode(String code) {
		for (Locale c: Locale.values()) {
			if (c.code.equalsIgnoreCase(code)) {
				return c;
			}
		}
		return null;
	}
}
