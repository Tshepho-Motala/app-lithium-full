package lithium.service.casino.provider.iforium.enums;

import lombok.Getter;

@Getter
public enum IForiumGameSuppliers {
    BLUEPRINT("Blueprint");

    private String gameSupplierName;

    IForiumGameSuppliers(String gameSupplierName) {
        this.gameSupplierName = gameSupplierName;
    }
}
