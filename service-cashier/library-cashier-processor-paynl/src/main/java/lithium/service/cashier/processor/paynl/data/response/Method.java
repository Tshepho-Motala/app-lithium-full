package lithium.service.cashier.processor.paynl.data.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Method {
    private String id;
    private String subId;
    private String name;
    private String image;
    private List<String> countryCodes;
    private List<String> subMethods;
}
