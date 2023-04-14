package lithium.service.translate.data.objects;

import lithium.service.translate.data.entities.TranslationKey;
import lithium.service.translate.data.entities.TranslationValue;
import lithium.service.translate.data.projections.TranslationValueWithoutKey;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Translation {

	TranslationKey key;
	TranslationValue referenceValue;
	TranslationValue value;
	
}
