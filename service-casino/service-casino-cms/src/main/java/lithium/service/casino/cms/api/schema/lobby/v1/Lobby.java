package lithium.service.casino.cms.api.schema.lobby.v1;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;
import java.util.List;

@Data
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Lobby {

	private Long id;

	private String name;

	private String description;

	private User createdBy;

	private Date createdDate;

	private User modifiedBy;

	private Date modifiedDate;

	private Integer version;

	private List<LobbyItem> lobbyItems;

}
