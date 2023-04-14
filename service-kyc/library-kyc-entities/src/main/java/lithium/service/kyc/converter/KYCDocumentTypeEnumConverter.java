package lithium.service.kyc.converter;

import lithium.service.kyc.entities.KYCDocumentType;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

public class KYCDocumentTypeEnumConverter {

    @Converter(autoApply = true)
    public static class FieldConverter implements AttributeConverter<KYCDocumentType, Integer> {

        @Override
        public Integer convertToDatabaseColumn(KYCDocumentType field) {
            return field.id();
        }

        @Override
        public KYCDocumentType convertToEntityAttribute(Integer id) {
            return KYCDocumentType.fromId(id);
        }
    }
}
