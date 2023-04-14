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
public class GenerateCsvRequest {
    private CsvProvider provider;
    private String domain;
    private Map<String, String> parameters;
    private Long reference;
}
