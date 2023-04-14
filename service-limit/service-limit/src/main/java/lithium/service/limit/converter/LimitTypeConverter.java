package lithium.service.limit.converter;

import lithium.service.limit.client.LimitType;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class LimitTypeConverter implements Converter<String, LimitType> {
    @Override
    public LimitType convert(String source) {
        try {
            return LimitType.fromType(Integer.parseInt(source));
        } catch (Exception e) {
            return null;
        }
    }
}
