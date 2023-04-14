package lithium.service.raf.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.ToString;

@ToString
@JsonFormat(shape=JsonFormat.Shape.OBJECT)
@AllArgsConstructor(access=AccessLevel.PRIVATE)
public enum RAFConversionType {
	DEPOSIT(0, "DEPOSIT"),
	XP_LEVEL(1, "XP_LEVEL");

	private Integer id;
	private String type;
	
	@JsonValue
	public Integer getId() { return id; }
	public void setId(Integer id) { this.id = id; }
	public String getType() { return this.type; }
	public void setType(String type) { this.type = type; }
	
	@JsonCreator
	public static RAFConversionType fromType(String type) {
		for (RAFConversionType t: RAFConversionType.values()) {
			if (t.type.equalsIgnoreCase(type)) {
				return t;
			}
			try {
				if (t.id.compareTo(Integer.valueOf(type)) == 0) {
					return t;
				}
			} catch (NumberFormatException nfe) {
				//Possible it is not a number who cares, we all love trying to parse bs values
			}
		}
		return null;
	}
	
	@JsonCreator
	public static RAFConversionType fromId(Integer id) {
		for (RAFConversionType t: RAFConversionType.values()) {
			if (t.id.compareTo(id) == 0) {
				return t;
			}
		}
		return null;
	}
}
