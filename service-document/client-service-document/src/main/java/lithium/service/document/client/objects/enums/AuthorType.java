package lithium.service.document.client.objects.enums;

public enum AuthorType {

    USER_DOCUMENT_EXTERNAL("user-document-external"),
    USER_DOCUMENT_INTERNAL("user-document-internal");

    private String typeName;
    AuthorType(String typeName) {
        this.typeName = typeName;
    }

    public String getTypeName() {
        return typeName;
    }
}
