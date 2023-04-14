package lithium.service.cashier.processor.paysafegateway.data;

public enum ThreeDResult {
    Y ("The cardholder successfully authenticated with their card issuer"),
    A ("The cardholder authentication was attempted"),
    N ("The cardholder failed to authenticate with their card issuer"),
    U ("Authentication with the card issuer was unavailable"),
    E ("An error occurred during authentication"),
    R ("Rejected transaction");
    private String description;
    ThreeDResult(String description) {
        this.description=description;
    }
    public String getDescription(){
        return description;
    }
}
