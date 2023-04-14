package lithium.service.limit.data.dto;

import lombok.Data;

import java.util.List;

@Data
public class EditDomainAgeLimitRange {
    private String domainName;
    private int previousAgeMin;
    private int previousAgeMax;
    private int nextAgeMin;
    private int nextAgeMax;
    private List<Long> idsToEdit = null;
}
