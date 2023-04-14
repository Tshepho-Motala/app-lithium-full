package lithium.util;

import lithium.math.CurrencyAmount;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

public class CurrencyJsonSerializer extends JsonSerializer<CurrencyAmount> {
    @Override
    public void serialize(CurrencyAmount value, JsonGenerator gen, SerializerProvider serializers) throws IOException,
            JsonProcessingException {
        gen.writeNumber(value.toAmount().toPlainString());
    }
}
