package lithium.service.cashier.client.frontend;

import com.fasterxml.jackson.annotation.JsonInclude;
import lithium.service.cashier.client.objects.ProcessorAccount;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
//@JsonIgnoreProperties(ignoreUnknown=true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProcessorAccountResponse {
    private String redirectUrl;
    private ProcessorAccount processorAccount;
    private String processorReference;
    private Long transactionId;
    @Builder.Default
    private ProcessorAccountResponseStatus status = ProcessorAccountResponseStatus.FAILED;
    private String errorCode;
    private String errorMessage;
    private String generalError;
}
