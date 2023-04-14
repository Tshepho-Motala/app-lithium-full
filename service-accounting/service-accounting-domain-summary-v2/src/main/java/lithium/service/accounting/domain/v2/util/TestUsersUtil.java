package lithium.service.accounting.domain.v2.util;

import java.util.ArrayList;
import java.util.List;

public class TestUsersUtil {

    public static List<Boolean> createIsTestUserList(Boolean isTestUsers) {
        ArrayList<Boolean> testUsers = new ArrayList<>();
        if (isTestUsers == null) {
            testUsers.add(true);
            testUsers.add(false);
        } else if (isTestUsers) {
            testUsers.add(true);
        } else {
            testUsers.add(false);
        }
        return testUsers;
    }

}
