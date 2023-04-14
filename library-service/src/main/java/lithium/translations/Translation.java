package lithium.translations;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Translation {

	private String code;
	private String translation;
	private String language;
	private int version;

}
