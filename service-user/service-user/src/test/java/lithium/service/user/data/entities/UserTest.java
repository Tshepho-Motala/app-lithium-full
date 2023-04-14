package lithium.service.user.data.entities;

import org.junit.Test;

import static org.junit.Assert.*;

public class UserTest {

    @Test
    public void cleanup1() {
        User user = new User();
        user.setFirstName(" Johan");
        user.setLastName(" van den Berg ");
        user.setDobDay(-1);
        user.setDobMonth(-1);
        user.setDobYear(-1);
        user.cleanup();
        assertEquals(user.getFirstName(), "Johan");
        assertEquals(user.getLastName(), "van den Berg");
        assertNull(user.getDobDay());
        assertNull(user.getDobMonth());
        assertNull(user.getDobYear());
    }

    @Test
    public void cleanup2() {
        User user = new User();
        user.setFirstName("  ");
        user.setLastName(" ");
        user.setDobDay(null);
        user.setDobMonth(null);
        user.setDobYear(null);
        user.cleanup();
        assertNull(user.getFirstName());
        assertNull(user.getLastName());
        assertNull(user.getDobDay());
        assertNull(user.getDobMonth());
        assertNull(user.getDobYear());
    }

}