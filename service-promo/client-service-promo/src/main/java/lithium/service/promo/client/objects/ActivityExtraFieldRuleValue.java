package lithium.service.promo.client.objects;

import java.io.Serial;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class ActivityExtraFieldRuleValue implements Serializable {

    @Serial
    private static final long serialVersionUID = -1253050498342470716L;
    private long id;
    private int version;

    @Builder.Default
    private List<String> value = new ArrayList<>();
    private ActivityExtraField activityExtraField;
}
