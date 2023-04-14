package lithium.service.cashier.mock.paynl.data.errors;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lithium.service.cashier.processor.paynl.data.response.Links;
import lithium.service.cashier.processor.paynl.exceptions.Error;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Errors {
    private Error general;
    private Transaction transaction;
    private Payment payment;
    private Error transactionId;
    @JsonProperty("_links")
    private List<Links> links;
}
