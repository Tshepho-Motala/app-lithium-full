package lithium.service.kyc.provider.onfido.objects;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;

public enum OnfidoReportType {
    DOCUMENT("document", Arrays.asList("national_identity_card", "driving_licence", "passport", "voter_id", "work_permit")),
    DOCUMENT_WITH_ADDRESS_INFORMATION("document_with_address_information", Arrays.asList("national_identity_card", "driving_licence", "passport", "voter_id", "work_permit")),
    DOCUMENT_WITH_DRIVING_LICENCE_INFORMATION("document_with_driving_licence_information", Arrays.asList("national_identity_card", "driving_licence", "passport", "voter_id", "work_permit")),
    PROOF_OF_ADDRESS("proof_of_address", Arrays.asList("bank_building_society_statement", "utility_bill",
            "electricity_bill", "water_bill", "gas_bill", "phone_bill", "internet_bill", "council_tax", "benefit_letters"));

    private String name;
    @Getter
    private List<String> documentTypes;

    OnfidoReportType(String name, List<String> documentTypes) {
        this.name = name;
        this.documentTypes = documentTypes;
    }

    public static OnfidoReportType fromName(String name) {
        for (OnfidoReportType type: values()) {
            if (type.name.equals(name)) {
                return type;
            }
        }
        return null;
    }
}
