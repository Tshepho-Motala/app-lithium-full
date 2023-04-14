package lithium.service.user.client.objects;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

@Data
@ToString(exclude = "userRevision")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = "userRevision")
public class UserRevisionLabelValue implements Serializable {
	private static final long serialVersionUID = 1L;

	private Long id;
	int version;
	@JsonBackReference
	private UserRevision userRevision;
	private LabelValue labelValue;
}
