package lithium.service.access.client.gamstop.objects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExclusionRequest {
    private String firstName;
    private String lastName;
    private String dateOfBirth;
    private String email;
    private String postcode;
    private String mobile;
    private String correlationId;
}
