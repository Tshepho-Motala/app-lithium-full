package lithium.service.geo.data.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Isp implements Serializable {
    private static final long serialVersionUID = 1L;
    Integer autonomousSystemNumber;
    String autonomousSystemOrganization;
    String isp;
    String organization;
}
