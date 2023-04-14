package lithium.service.user.provider.sphonic.idin.objects;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class IDINResponse {
    @JsonProperty
    SphonicResponse sphonicResponse;
}
