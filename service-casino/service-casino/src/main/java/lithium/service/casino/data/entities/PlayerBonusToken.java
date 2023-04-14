package lithium.service.casino.data.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.Date;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIdentityInfo(generator = ObjectIdGenerators.None.class, property = "id")
@Table(indexes = {
	@Index(name="idx_pbt_player_guid", columnList="user_id, status"),
	@Index(name="idx_pbt_status_expiry_date", columnList="status, expiryDate")
})
public class PlayerBonusToken {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Builder.Default
	@Column(nullable = false)
	private Date createdDate = new Date();

	@Builder.Default
	@Column(nullable = false)
	private Date expiryDate = new Date();

	@JoinColumn(nullable = false)
	@ManyToOne(fetch = FetchType.EAGER)
	private BonusToken bonusToken;

	@JoinColumn(nullable = false)
	@ManyToOne(fetch = FetchType.EAGER)
	private User user;

	//TODO: Map the enum in here for status
	@Column(nullable = false)
	private Integer status;

	private Long customTokenAmountCents; //The custom amount requirement is a bit strange, but there it is.

	private Long playerBonusHistoryId; //Not joining on this since it is only used to populate labels for bets
}
