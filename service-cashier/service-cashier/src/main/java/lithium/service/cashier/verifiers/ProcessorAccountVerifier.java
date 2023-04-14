package lithium.service.cashier.verifiers;

import lithium.service.cashier.client.objects.ProcessorAccount;
import lithium.service.cashier.client.objects.ProcessorAccountVerificationType;
import lithium.service.cashier.data.entities.User;

public interface ProcessorAccountVerifier {
    boolean verify(User cashierUser, ProcessorAccount processorAccount) throws Exception;
    ProcessorAccountVerificationType getType();
}
