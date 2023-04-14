package lithium.service.document.generation.data.objects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CsvGenerationStatus {
    private String status;
    private Long reference;
    private String comment;
    private Integer queuePosition;
}
