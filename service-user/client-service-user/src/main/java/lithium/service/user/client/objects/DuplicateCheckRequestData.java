package lithium.service.user.client.objects;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class DuplicateCheckRequestData implements Serializable {
    private String domainName;
    private String firstName;
    private String lastName;
    private int dobDay;
    private int dobMonth;
    private int dobYear;
    private long userOwnerId;
    private String postcode;
}
