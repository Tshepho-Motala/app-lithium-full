package lithium.service.promo.client.objects;

import java.io.Serial;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotNull;


@Data
@Builder
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class UserCategory implements Serializable {

    @Serial
    private static final long serialVersionUID = -2775215357238609789L;
    private Long id;
    private Long userCategoryId;

    @NotNull
    private UserCategoryType type;
    private PromotionRevision promotionRevision;
}
