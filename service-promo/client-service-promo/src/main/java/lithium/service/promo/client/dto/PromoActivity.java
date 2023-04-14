package lithium.service.promo.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class PromoActivity {
    private IActivity activity;

    @Builder.Default
    private Boolean requiresValue = Boolean.TRUE;

    List<ActivityExtraField> extraFields;
}
