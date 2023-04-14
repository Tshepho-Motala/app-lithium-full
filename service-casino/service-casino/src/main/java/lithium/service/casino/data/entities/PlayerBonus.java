package lithium.service.casino.data.entities;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Entity
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonIdentityInfo(generator = ObjectIdGenerators.None.class, property = "id")
@Table(indexes = {
		@Index(name = "idx_user_guid", columnList = "playerGuid", unique = true)
})
public class PlayerBonus {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	private String playerGuid;
	
	@JsonManagedReference("current")
	@ManyToOne(fetch = FetchType.EAGER, cascade=CascadeType.MERGE)
//	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(nullable=true)
	private PlayerBonusHistory current;

	@Transient Long balance;
	
}
