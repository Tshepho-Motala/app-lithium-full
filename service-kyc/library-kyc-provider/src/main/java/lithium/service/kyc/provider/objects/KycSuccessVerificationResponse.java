package lithium.service.kyc.provider.objects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KycSuccessVerificationResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    private String lastName;
	private String fullName;
	private String dob;
    private boolean dobYearOnly;

    private String providerRequestId;
    private Date createdOn;
    private String address;
    private String phoneNumber;
    private String countryOfBirth;
    private String nationality;
    private String methodTypeUid;
    private Integer kycDocumentType;
    private String documentBody;
    private String resultMessageText;
    private boolean success;
    private boolean manual;
	private String bvnUid;

    private String documentDecision;
    private String addressDecision;
    private String biometricValidation;
    private List<VendorData> vendorsData;
}
