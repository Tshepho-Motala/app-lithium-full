package lithium.service.kyc.provider.objects;

import java.util.List;
import java.util.Optional;

import static java.util.Objects.nonNull;
import static java.util.Optional.empty;

public final class VendorDataUtil {
    public static final String APPLICANT_ID = "applicantId";
    public static final String CREATED_AT = "createdAt";
    public static final String UPDATED_AT = "updatedAt";
    public static final String CHECK_SUMMARY = "checkSummary";
    public static final String DOCUMENT_REPORT = "documentReport";
    public static final String CHECK_ID = "checkId";
    public static final String FIRST_NAME = "firstName";
    public static final String LAST_NAME = "lastName";
    public static final String DATE_OF_BIRTH = "dateOfBirth";
    public static final String PLACE_OF_BIRTH = "placeOfBirth";
    public static final String ADDRESS = "address";
    public static final String ISSUING_COUNTRY = "issuingCountry";
    public static final String ISSUING_DATE = "issuingDate";
    public static final String DATE_OF_EXPIRY = "dateOfExpiry";
    public static final String DOCUMENT_TYPE = "documentType";
    public static final String DOCUMENT_NUMBER = "documentNumber";

    private VendorDataUtil() {
        throw new IllegalStateException("VendorDataUtil class");
    }

    public static Optional<String> findData(String key, List<VendorData> vendorData) {
        if (nonNull(vendorData) && !vendorData.isEmpty()) {
            return vendorData.stream()
                    .map(VendorData::getData)
                    .filter(data -> data.containsKey(key))
                    .map(data -> data.get(key))
                    .findFirst();
        }
        return empty();
    }
}
