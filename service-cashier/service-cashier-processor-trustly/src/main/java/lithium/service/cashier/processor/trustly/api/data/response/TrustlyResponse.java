package lithium.service.cashier.processor.trustly.api.data.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class TrustlyResponse {
    private String version;
    private Result result;
    private Error error;

    public String getVersion() {
        return version;
    }

    public void setVersion(final String version) {
        this.version = version;
    }

    public Result getResult() {
        return result;
    }

    public void setResult(final Result result) {
        this.result = result;
    }

    public Error getError() {
        return error;
    }

    public void setError(final Error error) {
        this.error = error;
    }

    public boolean successfulResult() {
        return result != null && error == null;
    }

    public String getUUID() {
        return successfulResult() ? result.getUuid() : error.getError().getUuid();
    }

    public String getSignature() {
        return successfulResult() ? result.getSignature() : error.getError().getSignature();
    }

    @Override
    public String toString() {
        return "VERSION: " + version +  "\nERROR: " + error +  "\nRESULT:\n" + result;
    }
}
