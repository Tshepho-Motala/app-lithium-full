package lithium.service.promo.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExclusiveAddRequest {
    private Long promotionId;
    private String sha256;

    @Builder.Default
    private List<String> players = new ArrayList<>();
}
