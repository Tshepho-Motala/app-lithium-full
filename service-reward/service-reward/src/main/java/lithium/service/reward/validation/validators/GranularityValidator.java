package lithium.service.reward.validation.validators;

import lithium.service.client.objects.Granularity;
import lithium.service.reward.validation.constraints.ValidGranularity;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GranularityValidator implements ConstraintValidator<ValidGranularity, Integer> {
    List<Granularity> granularityList;

    @Override
    public void initialize(ValidGranularity constraintAnnotation) {
        granularityList = new ArrayList<>();
        Collections.addAll(granularityList, constraintAnnotation.allowed());
    }
    @Override
    public boolean isValid(Integer granularityValue, ConstraintValidatorContext constraintValidatorContext) {
        if (granularityValue != null) {
            Granularity granularity = Granularity.fromGranularity(granularityValue);

            if (granularity != null) {
                return granularityList.contains(granularity);
            }
        }

        return false;
    }
}
