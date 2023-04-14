package lithium.util;

public enum NoteSection {

    CATEGORY("category"), SUBCATEGORY("subCategory"), PRIORITY("priority"), COMMENTS("comments");

    private final String section;
    NoteSection(String section) {
        this.section = section;
    }

    @Override
    public String toString() {
        return section;
    }
}
