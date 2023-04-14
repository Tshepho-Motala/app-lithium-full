package lithium.service.cashier.client.objects;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.Optional;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProcessorAccount {
    private Long id;
    private String userGuid;
    private String reference;
    private ProcessorAccountType type;
    private String methodCode;
    private String descriptor;
    private String providerData;
    private PaymentMethodStatusType status;
    private String name;
    private Map<String, String> data;
    private boolean hideInDeposit;
    private Boolean verified;
    private Boolean contraAccount;
    private ProcessorAccountVerificationType failedVerification;

    @JsonIgnore
    public boolean shouldSave() {
        return Optional.ofNullable(failedVerification).map(ProcessorAccountVerificationType::isSaveAccount).orElse(true);
    }
}
