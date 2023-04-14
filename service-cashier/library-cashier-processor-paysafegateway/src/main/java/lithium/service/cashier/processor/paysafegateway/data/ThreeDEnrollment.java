package lithium.service.cashier.processor.paysafegateway.data;

public enum ThreeDEnrollment {
    Y ("Cardholder authentication is available"),
    N ("Cardholder is not enrolled in authentication"),
    U ("Cardholder authentication unavailable");

    private String description;
    ThreeDEnrollment(String description) {
        this.description=description;
    }
    public String getDescription(){
        return  description;
    }
}
