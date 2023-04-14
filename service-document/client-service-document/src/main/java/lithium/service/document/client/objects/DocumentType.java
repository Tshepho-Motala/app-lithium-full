package lithium.service.document.client.objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "iconBase64")
@JsonIgnoreProperties(ignoreUnknown = true)
public class DocumentType {
    private Long id;
    private String purpose;
    private String type;
    private byte[] iconBase64;
    private String iconName;
    private String iconType;
    private Long iconSize;
    private boolean enabled;
    private Long modifiedDate;
    @Builder.Default
    private List<String> mappingNames = new ArrayList<>();
    private boolean typeSensitive;

    public List<String> getMappingNames() {
        if (isNull(mappingNames)) {
            mappingNames = new ArrayList<>();
        }
        return mappingNames;
    }
}
