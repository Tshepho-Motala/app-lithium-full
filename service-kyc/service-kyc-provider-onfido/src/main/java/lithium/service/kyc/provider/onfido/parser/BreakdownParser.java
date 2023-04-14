package lithium.service.kyc.provider.onfido.parser;

import java.util.Map;
import java.util.Optional;

public class BreakdownParser {
    private final Map<String, Object> breakdowns;
    private final Map<String, String> data;

    private BreakdownParser(Map<String, String> data, Map<String, Object> breakdowns) {
        this.data = data;
        this.breakdowns = breakdowns;
    }

    public static BreakdownParser of(Map<String, String> data, Map<String, Object> breakdowns) {
        return new BreakdownParser(data, breakdowns);
    }

    public void extract(String inputKey, String outputKey, String... filteredNames) {
        if (breakdowns.containsKey(inputKey)) {
            Map<String, Object> dataComparison = (Map<String, Object>) breakdowns.get(inputKey);
            if (dataComparison.containsKey("breakdown")) {
                Map<String, Object> breakdown = (Map<String, Object>) dataComparison.get("breakdown");
                for (String breakdownName : filteredNames) {
                    if (breakdown.containsKey(breakdownName)) {
                        data.put(outputKey + "." + breakdownName, parseBreakdown(breakdown.get(breakdownName)));
                    }
                }
            }
        }
    }

    private String parseBreakdown(Object breakdown) {
        Object result = ((Map<String, Object>) breakdown).get("result");
        return Optional.ofNullable(result).map(Object::toString).orElse(null);
    }
}
