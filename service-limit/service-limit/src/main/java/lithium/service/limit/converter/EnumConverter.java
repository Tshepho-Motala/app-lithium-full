package lithium.service.limit.converter;

import lithium.service.limit.client.objects.LossLimitsVisibility;
import lithium.service.limit.enums.AutoRestrictionRuleField;
import lithium.service.limit.enums.AutoRestrictionRuleOperator;
import lithium.service.limit.enums.AutoRestrictionRuleSetOutcome;
import lithium.service.limit.enums.ModifyType;
import lithium.service.limit.enums.RestrictionEvent;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EnumConverter {
	@Converter(autoApply=true)
	public static class OperatorConverter implements AttributeConverter<AutoRestrictionRuleOperator, Integer> {
		@Override
		public Integer convertToDatabaseColumn(AutoRestrictionRuleOperator operator) {
			return operator.id();
		}
		@Override
		public AutoRestrictionRuleOperator convertToEntityAttribute(Integer id) {
			return AutoRestrictionRuleOperator.fromId(id);
		}
	}

	@Converter(autoApply=true)
	public static class FieldConverter implements AttributeConverter<AutoRestrictionRuleField, Integer> {
		@Override
		public Integer convertToDatabaseColumn(AutoRestrictionRuleField field) {
			return field.id();
		}
		@Override
		public AutoRestrictionRuleField convertToEntityAttribute(Integer id) {
			return AutoRestrictionRuleField.fromId(id);
		}
	}

	@Converter(autoApply=true)
	public static class OutcomeConverter implements AttributeConverter<AutoRestrictionRuleSetOutcome, Integer> {
		@Override
		public Integer convertToDatabaseColumn(AutoRestrictionRuleSetOutcome outcome) {
			return outcome.id();
		}
		@Override
		public AutoRestrictionRuleSetOutcome convertToEntityAttribute(Integer id) {
			return AutoRestrictionRuleSetOutcome.fromId(id);
		}
	}

	@Converter(autoApply=true)
	public static class ModifyTypeConverter implements AttributeConverter<ModifyType, Integer> {
		@Override
		public Integer convertToDatabaseColumn(ModifyType type) {
			return type.id();
		}
		@Override
		public ModifyType convertToEntityAttribute(Integer id) {
			return ModifyType.fromId(id);
		}
	}

	@Converter(autoApply=true)
	public static class ResrtictionEventConverter implements AttributeConverter<RestrictionEvent, Integer> {
		@Override
		public Integer convertToDatabaseColumn(RestrictionEvent event) {
			return event != null ? event.id() : null;
		}
		@Override
		public RestrictionEvent convertToEntityAttribute(Integer id) {
			return RestrictionEvent.fromId(id);
		}
	}

	@Converter(autoApply = true)
	public static class LossLimitsVisibilityConverter implements AttributeConverter<LossLimitsVisibility, Integer> {

		@Override
		public Integer convertToDatabaseColumn(LossLimitsVisibility lossLimitsVisibility) {
			return lossLimitsVisibility.visibility();
		}

		@Override
		public LossLimitsVisibility convertToEntityAttribute(Integer visibility) {
			return LossLimitsVisibility.fromVisibility(visibility);
		}
	}
}
