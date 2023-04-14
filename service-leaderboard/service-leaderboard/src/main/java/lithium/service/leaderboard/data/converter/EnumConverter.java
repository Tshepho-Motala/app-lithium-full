package lithium.service.leaderboard.data.converter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import lithium.service.leaderboard.client.objects.Granularity;
import lithium.service.leaderboard.client.objects.Ordering;
import lithium.service.leaderboard.client.objects.Type;

public class EnumConverter {
	@Converter(autoApply = true)
	public static class TypeConverter implements AttributeConverter<Type, Integer> {
		@Override
		public Integer convertToDatabaseColumn(Type type) {
			return type.id();
		}
		@Override
		public Type convertToEntityAttribute(Integer id) {
			return Type.fromId(id);
		}
	}
	@Converter(autoApply = true)
	public static class OrderingConverter implements AttributeConverter<Ordering, Integer> {
		@Override
		public Integer convertToDatabaseColumn(Ordering ordering) {
			return ordering.id();
		}
		@Override
		public Ordering convertToEntityAttribute(Integer id) {
			return Ordering.fromId(id);
		}
		
	}
	@Converter(autoApply = true)
	public static class GranularityConverter implements AttributeConverter<Granularity, Integer> {
		@Override
		public Integer convertToDatabaseColumn(Granularity granularity) {
			return granularity.granularity();
		}
		@Override
		public Granularity convertToEntityAttribute(Integer granularity) {
			return Granularity.fromGranularity(granularity);
		}
	}
}
