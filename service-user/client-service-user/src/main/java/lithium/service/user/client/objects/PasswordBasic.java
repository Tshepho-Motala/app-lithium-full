package lithium.service.user.client.objects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PasswordBasic {
	private String currentPassword;
	private String newPassword;
	private String confirmPassword;

	public boolean confirmNewPasswordsMatch() {
		if (newPassword.equals(confirmPassword)) return true;
		return false;
	}
	public boolean newPasswordSameAsOld() {
		if (currentPassword.equals(newPassword)) return true;
		return false;
	}
}
