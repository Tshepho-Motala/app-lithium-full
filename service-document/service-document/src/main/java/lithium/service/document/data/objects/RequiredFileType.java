package lithium.service.document.data.objects;

public enum RequiredFileType {

    JPG("jpg"), JPEG("jpeg"), PNG("png"), PDF("pdf");

    private String type;

    RequiredFileType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
