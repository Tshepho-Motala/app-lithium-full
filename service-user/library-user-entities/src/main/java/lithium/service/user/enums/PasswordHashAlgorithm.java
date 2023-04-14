package lithium.service.user.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;

@ToString
@JsonFormat(shape=JsonFormat.Shape.OBJECT)
@AllArgsConstructor(access=AccessLevel.PRIVATE)
public enum PasswordHashAlgorithm {
	SHA1(0, "SHA-1"),
	PBKDF2(1, "PBKDF2");
	@Getter
	@Accessors(fluent=true)
	private Integer id;

	@Getter
	@Accessors(fluent=true)
	private String algorithm;

	@JsonCreator
	public static PasswordHashAlgorithm fromId(Integer id) {
		for (PasswordHashAlgorithm pha: PasswordHashAlgorithm.values()) {
			if (pha.id.equals(id)) {
				return pha;
			}
		}
		return null;
	}
}
