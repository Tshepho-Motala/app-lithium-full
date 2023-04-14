package lithium.service.cashier.processor.paynl.data.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceAddress {
    private String firstName;
    private String lastName;
    private String streetName;
    private String streetNumber;
    private String streetNumberExtension;
    private String zipCode;
    private String city;
    private String regionCode;
    private String countryCode;
}
