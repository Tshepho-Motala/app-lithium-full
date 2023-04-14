package lithium.service.document.generation.client.objects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CsvDataResponse {
    private List<? extends CsvContent> data;
    private int pages;
}
