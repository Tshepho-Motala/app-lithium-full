package lithium.service.cashier.processor.checkout.cc.data;

import com.checkout.payments.ThreeDSRequest;

public class ThreeDSRequestV2 extends ThreeDSRequest {
    private String version;
    private String exemption;
    private String challenge_indicator;
    private Boolean allow_upgrade;

    public ThreeDSRequestV2() {
        super();
    }

    public ThreeDSRequestV2(boolean enabled, Boolean attemptN3D, String eci, String cryptogram, String xid , String version, String exemption, String challenge_indicator, Boolean allow_upgrade) {
        super(enabled, attemptN3D, eci, cryptogram, xid);
        this.version = version;
        this.exemption = exemption;
        this.challenge_indicator = challenge_indicator;
        this.allow_upgrade = allow_upgrade;
    }


    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getExemption() {
        return exemption;
    }

    public void setExemption(String exemption) {
        this.exemption = exemption;
    }

    public String getChallenge_indicator() {
        return challenge_indicator;
    }

    public void setChallenge_indicator(String challenge_indicator) {
        this.challenge_indicator = challenge_indicator;
    }

    public Boolean getAllow_upgrade() {
        return allow_upgrade;
    }

    public void setAllow_upgrade(Boolean allow_upgrade) {
        this.allow_upgrade = allow_upgrade;
    }

    public String toString() {
        return "ThreeDSRequestV2(enabled=" + this.isEnabled() + ", attemptN3D=" + this.getAttemptN3D() + ", eci=" + this.getEci() + ", cryptogram=" + this.getCryptogram() + ", xid=" + this.getXid() + ", version=" + this.getVersion() + ", exemption=" + this.getExemption() + ", getChallenge_indicator=" + this.getChallenge_indicator() + ", allow_upgrade=" + this.getAllow_upgrade() + ")";
    }
}
