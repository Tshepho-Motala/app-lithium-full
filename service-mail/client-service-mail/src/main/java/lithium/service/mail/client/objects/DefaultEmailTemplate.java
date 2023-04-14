package lithium.service.mail.client.objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown=true)
public class DefaultEmailTemplate implements Serializable {
	private static final long serialVersionUID = 1968416892468579458L;
	
	private Long id;
	private int version;
	private String name;
	private String subject;
	private String body;
	private List<DefaultEmailTemplatePlaceholder> placeholders = new ArrayList<>();
}