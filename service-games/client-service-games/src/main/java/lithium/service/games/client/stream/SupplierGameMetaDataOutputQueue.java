package lithium.service.games.client.stream;


import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface SupplierGameMetaDataOutputQueue {

    @Output("suppliergamemetadataoutput")
    MessageChannel channel();
}
