package lithium.service.document.generation.converter;

import lithium.service.document.generation.client.objects.CsvProvider;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class CsvProviderEnumConverter implements AttributeConverter<CsvProvider, String> {
    @Override
    public String convertToDatabaseColumn(CsvProvider provider) {
        return provider.key();
    }

    @Override
    public CsvProvider convertToEntityAttribute(String name) {
        return CsvProvider.fromKey(name);
    }
}
