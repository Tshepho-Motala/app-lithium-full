package lithium.service.domain.client.objects;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class TemplateRevision implements Serializable {
	private static final long serialVersionUID = -7925201300721562427L;
	private Long id;
	private int version;
	private String content;
	private String description;
	private Template template;
}
