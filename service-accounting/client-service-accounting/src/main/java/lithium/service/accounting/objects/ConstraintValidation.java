package lithium.service.accounting.objects;

import lithium.service.accounting.enums.ConstraintValidationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ConstraintValidation {
    private ConstraintValidationType type;
    private String accountCode;
    private String accountTypeCode;
    private String labelName;
    private String labelValue;
}
