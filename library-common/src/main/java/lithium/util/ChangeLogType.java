package lithium.util;

public enum ChangeLogType {

    COMMENT("comment"), CREATE("create"), CREATED("created"), EDIT("edit"), ENABLE("enable");

    private String type;

    ChangeLogType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return type;
    }
}
