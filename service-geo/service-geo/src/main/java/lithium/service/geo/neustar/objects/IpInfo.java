package lithium.service.geo.neustar.objects;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@Builder
@ToString
public class IpInfo {

    @JsonProperty(value = "anonymizer_status")
    protected String anonymizerStatus;

    @JsonProperty(value = "Location")
    protected LocationType location;

    @JsonProperty(value = "NetworkData")
    protected NetworkDataType networkData;

}
