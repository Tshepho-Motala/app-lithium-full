package lithium.service.games.client.objects.supplier;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class SupplierGameMetaResults {
    private Long id;

    private String payoutLevel;

    private Boolean shield;

    private String value;

    private Integer multiplier;

    private String location;

    private String color;

    private String score;

    private String ties;

    private Boolean playerPair;

    private Boolean bankerPair;

    private Boolean natural;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<SupplierGameMetaResults> results;
}
