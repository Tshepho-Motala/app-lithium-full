package lithium.service.translate.data.objects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TranslationKeyRequest {
    private String messageType;
    private String messageKey;
    private String description;
    private String messageLanguage;
    private String domainName;
}


