package lithium.service.user.client.objects;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CollectionData {
    private String collectionName;
    private Map<String, String> data;
}
