package lithium.service.user.client.objects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;
import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserAttributesData {
	private String guid;
	private boolean testAccount;
	private Date createdDate;
    private Long statusId;
    private List<Long> playerTagIds;
}
