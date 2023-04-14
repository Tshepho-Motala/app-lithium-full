package lithium.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

@Slf4j
public class PlaceholderReplacerTest {
	@Test
	public void testReplace() {
		String text = "username %User.username%, email %User.email%";
		User user = new User("averygoodusername", "averygoodemail@test.com");
		assertEquals(PlaceholderReplacer.replace(text, user), "username averygoodusername,"
				+ " email averygoodemail@test.com");
	}
}

@Data
@AllArgsConstructor
class User {
	private String username;
	private String email;
}