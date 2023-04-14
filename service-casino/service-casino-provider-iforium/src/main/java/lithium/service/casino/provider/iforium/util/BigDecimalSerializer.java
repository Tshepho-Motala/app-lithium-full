package lithium.service.casino.provider.iforium.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class BigDecimalSerializer extends JsonSerializer<BigDecimal> {

    private static final int DECIMAL_SIZE = 2;

    @Override
    public void serialize(BigDecimal value, JsonGenerator jsonGenerator, SerializerProvider provider) throws IOException{
        jsonGenerator.writeString(value.setScale(DECIMAL_SIZE, RoundingMode.FLOOR).toString());
    }
}