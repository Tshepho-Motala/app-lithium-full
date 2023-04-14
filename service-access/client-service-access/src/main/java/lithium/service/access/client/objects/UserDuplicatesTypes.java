package lithium.service.access.client.objects;

public enum UserDuplicatesTypes {

    NAME_AND_DOB("Name and DOB"),
    LAST_NAME_AND_POSTCODE_AND_DOB("LastName, Postcode and DOB");

    private final String name;

    UserDuplicatesTypes(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }



    public static UserDuplicatesTypes getFromName(String name){
        for (UserDuplicatesTypes dt : UserDuplicatesTypes.values()) {
            if (dt.name.equalsIgnoreCase(name)) {
                return dt;
            }
        }
        return null;
    }
}
