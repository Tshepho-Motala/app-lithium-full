package lithium.service.limit.data.dto;

import lombok.Data;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@Data
public class RestrictionDto {
    private Long id;
    private int version;
    private String code;
    private String name;
}
