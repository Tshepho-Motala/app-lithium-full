package lithium.service.accounting.client.stream.auxlabel;

import lithium.service.accounting.objects.AuxLabelStreamData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Service
public class AuxLabelStream {
	
	@Autowired
	private AuxLabelOutputQueue channel;

	public void register(AuxLabelStreamData entry) {
		channel.auxLabelOutputQueue()
				.send(MessageBuilder.<AuxLabelStreamData>withPayload(entry)
						.build());
	}

}