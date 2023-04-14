package lithium.service.cashier.client.objects.transaction.dto;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIdentityInfo(generator = ObjectIdGenerators.None.class, property = "id")
@JsonIgnoreProperties(ignoreUnknown = true)
public class TransactionPaymentTypeDTO {
    private static final long serialVersionUID = -5791186162662490522L;

    private Long id;

    private String paymentType;
}
