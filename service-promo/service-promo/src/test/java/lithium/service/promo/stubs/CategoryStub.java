package lithium.service.promo.stubs;

import com.fasterxml.jackson.annotation.JsonFormat;
import lithium.service.promo.client.dto.ICategory;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.IOException;

@ToString
@JsonFormat( shape = JsonFormat.Shape.OBJECT )
@AllArgsConstructor( access = AccessLevel.PRIVATE )
public enum CategoryStub implements ICategory {
    CASINO("casino"),
    SPORTS("sports"),
    USER("user");

    @Getter
    @Setter
    private String category;
}
