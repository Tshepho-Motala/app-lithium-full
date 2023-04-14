package lithium.service.document.client.objects.enums;

public enum DocumentStatus {
    NEW("New"),
    DELETED("Deleted"),
    UPDATED("Updated");

    private String statusName;
    DocumentStatus(String statusName) {
        this.statusName = statusName;
    }

    public String getStatusName() {
        return statusName;
    }
}
