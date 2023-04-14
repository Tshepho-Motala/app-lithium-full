package lithium.service.games.provider.google.rge.client.objects.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PredictResponse {
    private List<Prediction> predictions;
    private String deployedModelId;
    private String model;
    private String modelDisplayName;
    private String modelVersionId;
}
