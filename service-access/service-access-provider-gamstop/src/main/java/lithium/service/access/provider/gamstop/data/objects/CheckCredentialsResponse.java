package lithium.service.access.provider.gamstop.data.objects;

import lombok.Data;

@Data
public class CheckCredentialsResponse {
    private boolean valid;
    private String url;
    private String apiKey;
}
