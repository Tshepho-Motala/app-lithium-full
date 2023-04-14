package lithium.service.access.provider.sphonic.schema.cruks.registration;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Name {
	private String firstNames;
	private String surname;
	private String surnamePreFix;
}
