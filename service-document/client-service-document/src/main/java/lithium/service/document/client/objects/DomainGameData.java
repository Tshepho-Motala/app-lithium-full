package lithium.service.document.client.objects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DomainGameData {
	String gameStartUrl;
	String imageUrl;
	String demoUrl;
	Iterable<Document> list;

}
