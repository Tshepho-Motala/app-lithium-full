package lithium.service.limit.data.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.List;
@Data
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PlayerLimitSetRequest {
    List<Integer> granularity;
    List<BigDecimal> amounts;
}
