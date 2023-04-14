package lithium.service.casino.client.objects.request;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CommandParams {
   private Map<String, String> paramsMap;
}
