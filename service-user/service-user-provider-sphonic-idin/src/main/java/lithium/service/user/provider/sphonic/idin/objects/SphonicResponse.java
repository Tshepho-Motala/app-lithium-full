package lithium.service.user.provider.sphonic.idin.objects;

import java.util.Map;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class SphonicResponse {
    Map<String, String> data;
}
