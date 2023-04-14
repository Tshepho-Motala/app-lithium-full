package lithium.service.casino.data.entities;

import java.util.Date;

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

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIdentityInfo(generator = ObjectIdGenerators.None.class, property = "id")
@Table(indexes = {
	@Index(name="idx_pbp_player_guid", columnList="playerGuid", unique=false)
})
public class PlayerBonusPending {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@Builder.Default
	@Column(nullable=false)
	private Date createdDate = new Date();
	private Long playThroughRequiredCents;
	private Long triggerAmount; //deposit cents
	
	private Long bonusAmount; //bonus amount
	//total amount in bonus pending account will be trigger amount + bonus amount
	private Integer bonusPercentage;
	
	@JoinColumn(nullable=true)
	@ManyToOne(fetch = FetchType.EAGER)
	private BonusRevision bonusRevision;
	
	private String playerGuid;

	private Long customFreeMoneyAmountCents; // This is used in manual trigger for now
}
