package lithium.service.geo.neustar.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lithium.service.geo.neustar.objects.GDSError;
import lithium.service.geo.neustar.objects.IpInfo;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@Builder
public class GeolocationResponse {

    @JsonProperty(value = "ipinfo")
    protected IpInfo ipInfo;

    @JsonProperty("gds_error")
    private GDSError gdsError;

}
