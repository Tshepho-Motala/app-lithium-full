package lithium.service.games.provider.google.rge.data.objects;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProviderSettingPredict {

    private String predictURL;

    private String project;

    private String location;

    private String endpoint;

    private Integer pageSize;

}
