package lithium.service.raf.data.objects;

import lithium.service.raf.data.enums.AutoConvertPlayer;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class AutoConvertRequest {
	
	@NotNull
	private AutoConvertPlayer autoConvertPlayer;
}
