package lithium.service.client.objects.placeholders;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Deprecated
public class LegacyPlaceholdersCreator {

	/*
		Create separate placeholder template
	 */

    public static Set<Placeholder> convertMap(Map<String, String> legacyPlaceHolder) {
        return legacyPlaceHolder.entrySet().stream()
                .map(entry -> new Placeholder(entry.getKey(), entry.getValue()))
                .collect(Collectors.toSet());
    }
}