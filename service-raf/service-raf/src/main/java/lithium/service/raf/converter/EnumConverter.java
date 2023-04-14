package lithium.service.raf.converter;

import lithium.service.raf.enums.RAFConversionType;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

public class EnumConverter {
	@Converter(autoApply=true)
	public static class RAFConversionTypeConverter implements AttributeConverter<RAFConversionType, Integer> {
		@Override
		public Integer convertToDatabaseColumn(RAFConversionType type) {
			return type.getId();
		}
		@Override
		public RAFConversionType convertToEntityAttribute(Integer id) {
			return RAFConversionType.fromId(id);
		}
	}
}
