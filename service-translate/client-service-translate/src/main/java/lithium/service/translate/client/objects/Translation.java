package lithium.service.translate.client.objects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Translation {
	String lang; 
	String code;
	String value;
	Domain domain;
	Module module;
	SubModule subModule;
}
