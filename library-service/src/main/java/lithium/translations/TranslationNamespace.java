package lithium.translations;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TranslationNamespace {
	
	private String code;
	private List<TranslationNamespace> childNamespaces = new ArrayList<TranslationNamespace>();
	private List<Translation> childTranslations = new ArrayList<Translation>();
	
}
