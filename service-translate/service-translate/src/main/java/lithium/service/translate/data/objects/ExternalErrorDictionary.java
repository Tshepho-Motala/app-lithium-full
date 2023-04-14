package lithium.service.translate.data.objects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExternalErrorDictionary {
    private String domainName;
    private String locale;
    private String errorMessageCode;
    private String errorMessageValue;
}