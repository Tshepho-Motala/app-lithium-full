package lithium.service.cashier.data.objects;

import lithium.service.cashier.data.entities.Image;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ProcessorAccountDetails implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private Image processorIcon;
    private String name;
    private String nameOnPayEntry;
    private BigDecimal depositSum;
    private BigDecimal withdrawSum;
    private BigDecimal netDeposit;
    private String currencyCode;
    private ProcessorAccountStatus status;
    private Boolean verified;
    private String verificationError;
    private Boolean contraAccount;
    private String createdOn;
    private String expiryDate;
}
