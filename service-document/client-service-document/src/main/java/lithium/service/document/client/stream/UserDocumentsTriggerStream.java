package lithium.service.document.client.stream;

import lithium.service.user.client.objects.UserDocumentData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Service
public class UserDocumentsTriggerStream {
	@Autowired
	private UserDocumentsTriggerOutputQueue queue;

	public void trigger(UserDocumentData data) {
		queue.outputQueue().send(MessageBuilder.<UserDocumentData>withPayload(data).build());
	}
}
