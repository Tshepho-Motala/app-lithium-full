package lithium.service.cashier.converter;


import lithium.service.cashier.client.objects.enums.AutoWithdrawalRuleOperator;
import lithium.service.cashier.client.objects.enums.AutoWithdrawalRuleType;
import lithium.service.cashier.client.objects.enums.AutoWithdrawalRuleFieldType;
import lithium.service.cashier.client.objects.enums.TransactionTagType;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;


public class EnumConverter {

  @Converter(autoApply = true)
  public static class OperatorConverter implements AttributeConverter<AutoWithdrawalRuleOperator, Integer> {

    @Override
    public Integer convertToDatabaseColumn(AutoWithdrawalRuleOperator operator) {
      return operator.id();
    }

    @Override
    public AutoWithdrawalRuleOperator convertToEntityAttribute(Integer id) {
      return AutoWithdrawalRuleOperator.fromId(id);
    }
  }

  @Converter(autoApply = true)
  public static class RuleTypeConverter implements AttributeConverter<AutoWithdrawalRuleType, Integer> {

    @Override
    public Integer convertToDatabaseColumn(AutoWithdrawalRuleType ruleType) {
      return ruleType.id();
    }

    @Override
    public AutoWithdrawalRuleType convertToEntityAttribute(Integer id) {
      return AutoWithdrawalRuleType.fromId(id);
    }
  }

  @Converter(autoApply = true)
  public static class AutoWithdrawalRuleFieldTypeConverter implements AttributeConverter<AutoWithdrawalRuleFieldType, String> {

    @Override
    public String convertToDatabaseColumn(AutoWithdrawalRuleFieldType fieldType) {
      return fieldType.name();
    }

    @Override
    public AutoWithdrawalRuleFieldType convertToEntityAttribute(String name) {
      return AutoWithdrawalRuleFieldType.fromName(name);
    }
  }

  @Converter(autoApply = true)
  public static class TransactionTagTypeConverter implements AttributeConverter<TransactionTagType, Integer> {

    @Override
    public Integer convertToDatabaseColumn(TransactionTagType ruleType) {
      return ruleType.getId();
    }

    @Override
    public TransactionTagType convertToEntityAttribute(Integer id) {
      return TransactionTagType.fromId(id);
    }
  }
}
