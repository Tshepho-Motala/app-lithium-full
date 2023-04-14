package lithium.service.document.generation.client.objects;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor
public class CommonCommandParams implements CommandParams {
    private Map<String, String> paramsMap;
}
