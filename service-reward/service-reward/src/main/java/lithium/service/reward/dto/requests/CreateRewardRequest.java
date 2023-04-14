package lithium.service.reward.dto.requests;

import lithium.service.client.objects.Granularity;
import lithium.service.reward.dto.SimpleRewardType;
import lithium.service.reward.dto.SimpleRewardTypeGame;
import lithium.service.reward.dto.SimpleRewardTypeValue;
import lithium.service.reward.validation.constraints.ValidGranularity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateRewardRequest {

    @NotBlank(message = "name is a required field")
    private String name;

    @NotBlank(message = "code is a required field")
    private String code;

    private String description;

    @NotBlank(message = "domainName is a required field")
    private String domainName;


    @Min(value = 1)
    private Integer validFor;

    @ValidGranularity(allowed = {
            Granularity.GRANULARITY_DAY,
            Granularity.GRANULARITY_WEEK,
            Granularity.GRANULARITY_MONTH,
            Granularity.GRANULARITY_YEAR,
            Granularity.GRANULARITY_TOTAL }
    )

    private Integer validForGranularity;

    @Valid
    private List<SimpleRewardType> rewardTypes;
}
