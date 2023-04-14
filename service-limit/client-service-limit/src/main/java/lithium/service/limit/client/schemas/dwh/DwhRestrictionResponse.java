package lithium.service.limit.client.schemas.dwh;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import java.util.List;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class DwhRestrictionResponse {
    private Long id;
    private String name;
    private List<String> restrictions;
}
