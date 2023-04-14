package lithium.service.casino.provider.roxor.enums;

import lombok.Getter;

@Getter
public enum RoxorGameSuppliers {
    ROXOR("Roxor Gaming"),
    MICRO_GAMING("Games Global");

    private String gameSupplierName;

    RoxorGameSuppliers(String gameSupplierName) {
        this.gameSupplierName = gameSupplierName;
    }
}
