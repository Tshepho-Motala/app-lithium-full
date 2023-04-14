package lithium.service.promo.objects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Data
@Accessors(fluent = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PromoQuery {
    private LocalDate startDate;
    private LocalDate endDate;
    private List<String> domains;
    private Boolean exclusive;
}
