package lithium.service.document.client.objects.enums;

public enum DocumentType {
    VERIFICATION_DOCUMENT("Verification Document"),
    IDENTITY_DOCUMENT("Identification Document"),
    PROOF_OF_RESIDENCE("Proof Of Residence");

    private String typeName;
    DocumentType(String typeName) {
        this.typeName = typeName;
    }

    public String getTypeName() {
        return typeName;
    }
}
