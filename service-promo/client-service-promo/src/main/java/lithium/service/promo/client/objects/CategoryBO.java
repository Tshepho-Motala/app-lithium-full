package lithium.service.promo.client.objects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;

@Data
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class CategoryBO implements Serializable {
    @Serial
    private static final long serialVersionUID = -8681380031382674159L;

    public Long id;
    public String name;
}
