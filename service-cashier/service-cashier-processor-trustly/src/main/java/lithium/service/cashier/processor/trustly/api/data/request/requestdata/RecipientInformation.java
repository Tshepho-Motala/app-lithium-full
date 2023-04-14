package lithium.service.cashier.processor.trustly.api.data.request.requestdata;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lithium.service.cashier.processor.trustly.api.data.request.AttributeData;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RecipientInformation extends AttributeData {
    @JsonProperty("Partytype")
    private String partyType;
    @JsonProperty("Firstname")
    private String firstName;
    @JsonProperty("Lastname")
    private String lastName;
    @JsonProperty("CountryCode")
    private String countryCode;
    @JsonProperty("CustomerID")
    private String customerId;
    @JsonProperty("Address")
    private String address;
    @JsonProperty("DateOfBirth")
    private String dateOfBirth;
}
