package lithium.service.cashier.processor.trustly.api.data.request.requestdata;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SenderInformation {
    @JsonProperty("Partytype")
    private String partyType;
    @JsonProperty("Address")
    private String address;
    @JsonProperty("CountryCode")
    private String countryCode;
    @JsonProperty("Firstname")
    private String firstName;
    @JsonProperty("Lastname")
    private String lastName;
}
