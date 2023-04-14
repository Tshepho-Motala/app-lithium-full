package lithium.service.user.client.objects;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ValidatePreRegistrationResponse {
    private boolean registrationAllowed;
    private int lastStageCompleted;
    private boolean cellphoneValidated;
    private boolean emailValidated;
    private boolean isKycSuccess;
    private String userGuid;

    public boolean getRegistrationAllowed() {
        return registrationAllowed;
    }

    public void setRegistrationAllowed(boolean registrationAllowed) {
        this.registrationAllowed = registrationAllowed;
    }

    public void setLastStageCompleted(int lastStageCompleted) {
        this.lastStageCompleted = lastStageCompleted;
    }

    public int getLastStageCompleted() {
        return lastStageCompleted;
    }

    public boolean isEmailValidated() {
        return cellphoneValidated;
    }

    public boolean isCellphoneValidated() {
        return cellphoneValidated;
    }

    public void setEmailValidated(boolean emailValidated) {
        this.emailValidated = emailValidated;
    }

    public void setCellphoneValidated(boolean cellphoneValidated) {
        this.cellphoneValidated = cellphoneValidated;
    }

    public void setKycSuccess(boolean kycSuccess) {
        isKycSuccess = kycSuccess;
    }

    public boolean isKycSuccess() {
        return isKycSuccess;
    }
}
