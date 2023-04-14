package lithium.service.document.generation.data.objects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentGenerateBO {
    private Long id;
    private String authorGuid;
    private CsvGenerationStatus status;
}
