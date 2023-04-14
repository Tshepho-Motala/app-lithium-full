package lithium.service.kyc.provider.objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class VerifyRequest {
    private VerificationMethodType verificationMethodName;
    private List<VerifyParam> fields;

    public Map<String, String> getFieldsAsMap() {
        return fields
                .stream()
                .filter(param -> nonNull(param.getKey()) && nonNull(param.getValue()))
                .collect(Collectors.toMap(VerifyParam::getKey, VerifyParam::getValue));
    }
}
