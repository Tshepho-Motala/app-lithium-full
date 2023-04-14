package lithium.service.user.converter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import lithium.service.client.objects.Granularity;
import lithium.service.user.data.entities.playtimelimit.LimitType;
import lithium.service.user.client.enums.BiometricsStatus;
import lithium.service.user.enums.PasswordHashAlgorithm;
import lithium.service.user.enums.Type;

public class EnumConverter {

  @Converter(autoApply = true)
  public static class UserPasswordTokenCommsTypeConverter implements AttributeConverter<Type, String> {

    @Override
    public String convertToDatabaseColumn(Type type) {
      return type.type();
    }

    @Override
    public Type convertToEntityAttribute(String type) {
      return Type.fromType(type);
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

  @Converter(autoApply = true)
  public static class LimitTypeConverter implements AttributeConverter<LimitType, Integer> {

    @Override
    public Integer convertToDatabaseColumn(LimitType limitType) {
      return limitType.type();
    }

    @Override
    public LimitType convertToEntityAttribute(Integer limitType) {
      return LimitType.fromType(limitType);
    }
  }


  @Converter(autoApply = true)
  public static class BiometricsStatusConverter implements AttributeConverter<BiometricsStatus, Integer> {

    @Override
    public Integer convertToDatabaseColumn(BiometricsStatus status) {
      return status.code();
    }

    @Override
    public BiometricsStatus convertToEntityAttribute(Integer status) {
      return BiometricsStatus.fromCode(status);
    }
  }

  @Converter(autoApply = true)
  public static class PasswordHashAlgorithmConverter implements AttributeConverter<PasswordHashAlgorithm, Integer> {
    @Override
    public Integer convertToDatabaseColumn(PasswordHashAlgorithm passwordHashAlgorithm) {
      return passwordHashAlgorithm.id();
    }

    @Override
    public PasswordHashAlgorithm convertToEntityAttribute(Integer passwordHashAlgorithmId) {
      return PasswordHashAlgorithm.fromId(passwordHashAlgorithmId);
    }
  }
}
