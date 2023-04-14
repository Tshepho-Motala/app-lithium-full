package lithium.service.datafeed.provider.google.objects;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PubSubMessage {
    private Long timestamp;
    private Object data;
    private DataType dataType;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String eventType;
    private String domainName;
}
