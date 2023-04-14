package lithium.service.kyc.provider.onfido.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.util.Objects.isNull;

@Service
@Slf4j
public class AddressMatchService {

    public boolean strictMatch(String logIdentifier, String extractedAddress, String addressLine1, String city, String postalCode) {
        return match(logIdentifier, extractedAddress, addressLine1, city, postalCode, new ArrayList<>());
    }

    public boolean matchWithSymbolsIgnore(String logIdentifier, String extractedAddress, String addressLine1, String city, String postalCode, String... cleanSymbols) {
        return match(logIdentifier, extractedAddress, addressLine1, city, postalCode, Arrays.stream(cleanSymbols).toList());
    }
    public boolean match(String logIdentifier, String extractedAddress, String addressLine1, String city, String postalCode, List<String> cleanSymbols) {
        if (isNull(extractedAddress)) {
            log.warn(logIdentifier + ", extractedAddress is null");
            return false;
        }
        if (isNull(addressLine1)) {
            log.warn(logIdentifier + ", addressLine1 is null");
            return false;
        }
        if (isNull(city)) {
            log.warn(logIdentifier + ", city is null");
            return false;
        }
        if (isNull(postalCode)) {
            log.warn(logIdentifier + ", postalCode is null");
            return false;
        }

        List<String> fields = Arrays.asList(addressLine1, city, postalCode);

        String addressLine = fields.get(0).split(",")[0];
        fields.set(0, addressLine);

        for (String symbol : cleanSymbols) {
            extractedAddress = extractedAddress.replaceAll(symbol,"");
        }

        for (String field : fields) {
            for (String symbol : cleanSymbols) {
                field = field.replaceAll(symbol,"");
            }

            boolean isMatch = matchString(logIdentifier, extractedAddress, field);
            if (!isMatch) {
                return false;
            }
            extractedAddress = extractedAddress.replaceFirst(field, "");
        }
        return true;
    }

    private boolean matchString(String logIdentifier, String target, String substring) {
        if (target.toLowerCase().contains(substring.toLowerCase())) {
            return true;
        }
        log.debug(logIdentifier + ", >" + substring + "< not match in: " + target);
        return false;
    }
}
