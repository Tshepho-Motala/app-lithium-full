package lithium.service.limit.objects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserRestrictionsRequest {
    @NotNull(message = "This field is required")
    @NotBlank(message = "This is field cannot be empty")
    private String userGuid;

    @Min(value = 1, message = "Please provide a valid userId")
    private long userId;

    @NotNull(message = "This field is required")
    @NotEmpty(message = "Please provide values for this field")
    private List<Long> domainRestrictionSets;

    @NotNull(message = "This field is required")
    @NotBlank(message = "This is field cannot be empty")
    private String comment;
    private Integer subType;

}
