package lithium.service.document.generation.client.objects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CsvGenerationJob {
    private Map<String, String> parameters;
    private Long reference;
}
