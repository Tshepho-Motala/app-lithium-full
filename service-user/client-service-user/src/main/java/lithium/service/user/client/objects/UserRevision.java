package lithium.service.user.client.objects;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = "user")
public class UserRevision implements Serializable {
	private static final long serialVersionUID = 1L;

	private Long id;
	int version;
	@JsonBackReference
	private User user;
	private Date creationDate;
	@JsonManagedReference
	private List<UserRevisionLabelValue> labelValueList;
}
