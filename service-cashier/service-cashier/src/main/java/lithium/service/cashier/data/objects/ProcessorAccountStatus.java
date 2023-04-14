package lithium.service.cashier.data.objects;

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
public class ProcessorAccountStatus implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer id;
    private String name;
}
