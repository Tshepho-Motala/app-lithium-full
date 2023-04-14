package lithium.service.access.client.objects;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import lombok.NoArgsConstructor;

@Data
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class SimpleUserDuplicateType {
    private String name;
    private String value;
}
