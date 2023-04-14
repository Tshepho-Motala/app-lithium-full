package lithium.service.cashier.processor.checkout.cc.data;

import com.checkout.payments.IdSource;
import lithium.service.cashier.client.internal.SensitiveData;

public class CvvMaskedIdSource extends IdSource {

    public CvvMaskedIdSource(String id) {
        super(id);
    }

    @Override
    public String toString() {
        return "IdSource{id=" + super.getId() + ", cvv=" + SensitiveData.CVV.applyMask(super.getCvv()) + ", type=" + super.getType() + "}";
    }
}
