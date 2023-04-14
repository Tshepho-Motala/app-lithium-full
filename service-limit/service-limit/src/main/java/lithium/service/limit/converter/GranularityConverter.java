package lithium.service.limit.converter;

import lithium.service.client.objects.Granularity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class GranularityConverter implements Converter<String, Granularity> {
    @Override
    public Granularity convert(String source) {
        try {
            return Granularity.fromGranularity(Integer.parseInt(source));
        } catch (Exception e) {
            return null;
        }
    }
}
