package lithium.service.casino.provider.roxor.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Metadata {

    private String nodeName;
    private String eventId;
    private String requestId;
    private Long eventTime;
    private String podName;
    private String eventType;
    private String componentName;
    private String componentVersion;

}
