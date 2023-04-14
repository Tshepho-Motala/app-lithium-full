package lithium.service.cashier.processor.paysafegateway.data;

public enum ThreeDCardType {
    VI("VISA"), MC("Master Card");
    private String description;


    ThreeDCardType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
