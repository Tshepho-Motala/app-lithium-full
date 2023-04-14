package lithium.service.user.data.objects;

import lombok.Data;

@Data
public class UserBasic {
	
	private String domain;
	private String username;
	private String password;
	private String email;
	private String firstName;
	private String lastName;
	
}
