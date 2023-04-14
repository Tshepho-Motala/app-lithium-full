package lithium.service.cashier.client.objects.transaction.dto;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.HashMap;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIdentityInfo(generator = ObjectIdGenerators.None.class, property = "id")
public class DomainMethodProcessor implements Serializable {
    private static final long serialVersionUID = 1179219034991487630L;

    private Long id;
    private int version;

    private Double weight;
    private Processor processor;
    private DomainMethod domainMethod;
    private Boolean enabled;
    private Boolean deleted;
    private String accessRule;
    private Boolean reserveFundsOnWithdrawal;
    private String description;

    private HashMap<String, String> properties;
}