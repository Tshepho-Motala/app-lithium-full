package lithium.service.casino.client.objects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Game implements Serializable {

    private String name;
    private String guid;
    private GameSupplier gameSupplier;
    private String moduleSupplierId;

}
