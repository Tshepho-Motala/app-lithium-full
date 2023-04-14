package lithium.service.document.generation.client.objects;

import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class CsvGenerationDataResponse {

    private final Long reference;
    private int totalElements;
    private byte[] data;

    public CsvGenerationDataResponse(Long reference) {
        this.reference = reference;
    }
}
