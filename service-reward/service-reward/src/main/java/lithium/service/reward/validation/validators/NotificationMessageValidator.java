package lithium.service.reward.validation.validators;

import lithium.service.reward.validation.constraints.ValidRewardNotificationMessage;
import lithium.util.StringUtil;
import lombok.SneakyThrows;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapperImpl;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class NotificationMessageValidator implements ConstraintValidator<ValidRewardNotificationMessage, Object> {
    @SneakyThrows
    @Override
    public boolean isValid(Object object, ConstraintValidatorContext constraintValidatorContext) {
        BeanWrapperImpl beanWrapper = new BeanWrapperImpl(object);
        boolean instant = (boolean) beanWrapper.getPropertyValue("instant");
        String message = (String) beanWrapper.getPropertyValue( "notificationMessage");

        if (!instant) {
            return !StringUtil.isEmpty(message);
        }

        return true;
    }
}
