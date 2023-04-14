package lithium.service.user.client.objects;

import lombok.Builder;

@Builder
public class ValidatePostRegistrationResponse {
    private boolean registrationAllowed;
    private int lastStageCompleted;
    private boolean isCellphoneValidated;
    private boolean isEmailValidated;
    private boolean isKycSuccess;

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
        return isEmailValidated;
    }

    public boolean isCellphoneValidated() {
        return isCellphoneValidated;
    }

    public void setEmailValidated(boolean isEmailValidated) {
        this.isEmailValidated = isEmailValidated;
    }

    public void setCellphoneValidated(boolean isCellphoneValidated) {
        this.isCellphoneValidated = isCellphoneValidated;
    }

    public void setKycSuccess(boolean kycSuccess) {
        isKycSuccess = kycSuccess;
    }

    public boolean isKycSuccess() {
        return isKycSuccess;
    }
}
