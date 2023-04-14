package lithium.service.translate.data.projections;

import lithium.service.translate.data.entities.Language;
import lithium.service.translate.data.entities.TranslationValueDefault;
import lithium.service.translate.data.entities.TranslationValueRevision;

public interface TranslationValueWithoutKey {

	Long getId();
	TranslationValueDefault getDefaultValue();
	TranslationValueRevision getCurrent();
	Language getLanguage();
	
}
