package lithium.service.sms.client.objects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class DefaultSMSTemplate implements Serializable {
	private static final long serialVersionUID = -1850287516456568409L;
	
	private Long id;
	private int version;
	private String name;
	private String description;
	private String text;
	private List<DefaultSMSTemplatePlaceholder> placeholders = new ArrayList<>();
}