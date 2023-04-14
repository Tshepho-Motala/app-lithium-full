package lithium.service.geo.objects;

import lithium.service.geo.data.entities.ConnectionType;
import lithium.service.geo.data.entities.Isp;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

@Data
@Builder
@ToString
public class Network implements Serializable {

    private static final long serialVersionUID = 1L;

    Isp isp;
    ConnectionType connectionType;
}
