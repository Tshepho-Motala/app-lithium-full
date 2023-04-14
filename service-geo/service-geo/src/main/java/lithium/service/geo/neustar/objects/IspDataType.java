package lithium.service.geo.neustar.objects;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Builder
@ToString
@Data
public class IspDataType {
    Integer autonomousSystemNumber;
    String autonomousSystemOrganization;
    String isp;
    String organization;
}
