package lithium.service.user.client.objects;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
public class Device {
    /**
     * The device operating system that was used
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String os;
    /**
     * The type of browser that was used i.e chrome, safari etc
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String browser;
    /**
     * The type of client that made the request
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String userAgent;
    /**
     * The ipAddress the device was connected to
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String ipAddress;
    /**
     * Authentication client
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String providerAuthClient;
}
