package lithium.service.promo.client.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import lithium.service.promo.client.dto.ICategory;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.IOException;
import java.util.Arrays;

@ToString
@JsonFormat( shape = JsonFormat.Shape.OBJECT )
@AllArgsConstructor( access = AccessLevel.PRIVATE )
public enum PromoCategory implements ICategory {
    CASINO("casino"),
    SPORT("sport");

    @Setter
    @Accessors( fluent = true )
    private String category;

    @Override
    public String getCategory() throws IOException {
        return category;
    }
}
