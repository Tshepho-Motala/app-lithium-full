package lithium.service.cashier.processor.paysafegateway.data;

public enum ECIStatus {
    VI_5(5,"Identifies a successfully authenticated transaction"),
    VI_6(6,"Identifies an attempted authenticated transaction"),
    VI_7(7,"Identifies a non-authenticated transaction"),
    MC_1(1,"Identifies a non-authenticated transaction"),
    MC_2(2,"Identifies a successfully authenticated transaction");

    private int eci;
    private String description;

    ECIStatus(int eci, String description) {
        this.eci = eci;
        this.description= description;
    }

    public String getDescription(){
        return description;
    }
    public int getEci(){
        return eci;
    }
}
