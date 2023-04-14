package lithium.service.pushmsg.client.internal;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@ToString
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum DoProviderResponseStatus {
	SUCCESS(0),
	FAILED(1),
	PENDING(2);
	
	@Getter
	@Setter
	@Accessors(fluent = true)
	private Integer code;
	
	@JsonCreator
	public static DoProviderResponseStatus fromCode(int code) {
		for (DoProviderResponseStatus g : DoProviderResponseStatus.values()) {
			if (g.code == code) {
				return g;
			}
		}
		return FAILED;
	}
}