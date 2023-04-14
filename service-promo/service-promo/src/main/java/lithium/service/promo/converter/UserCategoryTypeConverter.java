package lithium.service.promo.converter;

import lithium.service.promo.client.objects.UserCategoryType;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class UserCategoryTypeConverter implements AttributeConverter<UserCategoryType, String> {
    @Override
    public String convertToDatabaseColumn(UserCategoryType userCategoryType) {
        return userCategoryType.getType();
    }

    @Override
    public UserCategoryType convertToEntityAttribute(String type) {
        return UserCategoryType.fromType(type);
    }
}
