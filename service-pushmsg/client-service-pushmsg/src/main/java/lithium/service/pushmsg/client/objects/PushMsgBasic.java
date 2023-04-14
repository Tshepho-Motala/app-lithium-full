package lithium.service.pushmsg.client.objects;

import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder=true)
public class PushMsgBasic {
	private String domainName;
	private String templateId;
//	private String heading;
//	private String content;
	@Builder.Default
	private String language = "en";
	private Integer priority;
	private List<String> userGuids;
	private Map<String, String> placeholders;
}