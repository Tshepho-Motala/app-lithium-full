package lithium.service.user.client.objects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserDocumentData implements Serializable {
	private static final long serialVersionUID = -1;
	private long documentId;
	private String guid;
	private long statusId;
	private String statusName;
	private boolean sensitive;
	private boolean deleted;
}

