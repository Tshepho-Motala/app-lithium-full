package lithium.service.accounting.provider.internal.data.projection.entities;

public interface TransactionLabelValueProjection {
    Long getTransactionId();
    String getLabelName();
    String getLabelValue();

}

