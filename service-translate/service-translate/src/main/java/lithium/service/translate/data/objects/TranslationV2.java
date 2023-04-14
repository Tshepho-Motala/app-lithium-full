package lithium.service.translate.data.objects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TranslationV2 {

    private String domainName;
    private String language;
    private Long keyId;
    private String key;
    private Long valueId;
    private String value;

}