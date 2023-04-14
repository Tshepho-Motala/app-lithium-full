package lithium.service.casino.cms.api.objects;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageBannerRequest {
    private String primaryNavCode;
    private String secondaryNavCode;
    private String channel;
}
