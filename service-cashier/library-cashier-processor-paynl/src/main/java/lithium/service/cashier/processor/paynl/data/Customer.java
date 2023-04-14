package lithium.service.cashier.processor.paynl.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lithium.service.cashier.processor.paynl.data.response.BankAccount;
import lithium.service.cashier.processor.paynl.data.response.Company;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Customer {
    private String firstName;
    private String lastName;
    private String ipAddress;
    private String birthDate;
    private String gender;
    private String phone;
    private String email;
    private String language;
    private String trust;
    private String reference;
    private BankAccount bankAccount;
    private Company company;
}
