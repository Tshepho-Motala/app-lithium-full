package lithium.service.document.data.converter;

import lithium.service.document.client.objects.DocumentPurpose;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.Optional;

public class EnumConverter {
	@Converter(autoApply=true)
	public static class DocumentPurposeConverter implements AttributeConverter<DocumentPurpose, Integer> {
		@Override
		public Integer convertToDatabaseColumn(DocumentPurpose documentPurpose) {
			return Optional.ofNullable(documentPurpose).map(DocumentPurpose::id).orElse(null);
		}
		@Override
		public DocumentPurpose convertToEntityAttribute(Integer id) {
			return DocumentPurpose.fromId(id);
		}
	}
}
