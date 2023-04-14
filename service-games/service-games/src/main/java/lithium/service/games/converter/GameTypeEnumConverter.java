package lithium.service.games.converter;

import lithium.service.games.data.entities.GameTypeEnum;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class GameTypeEnumConverter implements AttributeConverter<GameTypeEnum, String> {
    @Override
    public String convertToDatabaseColumn(GameTypeEnum attribute) {
        return attribute.getValue();
    }

    @Override
    public GameTypeEnum convertToEntityAttribute(String dbData) {
        return GameTypeEnum.fromType(dbData);
    }
}
