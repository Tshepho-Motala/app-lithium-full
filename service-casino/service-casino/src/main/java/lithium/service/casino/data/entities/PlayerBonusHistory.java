package lithium.service.casino.data.entities;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude={"playerBonus"})
@EqualsAndHashCode(exclude={"playerBonus"})
@JsonIgnoreProperties(value={"handler", "hibernateLazyInitializer", "playerBonus"})
@JsonIdentityInfo(generator = ObjectIdGenerators.None.class, property = "id")
public class PlayerBonusHistory {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@Column(nullable=false)
	@Builder.Default
	private Date startedDate = new Date();
	private Long playThroughCents;
	private Long playThroughRequiredCents;
	private Long triggerAmount;
	
	private Long bonusAmount;
	private Integer bonusPercentage;
	
	private Boolean completed;
	private Boolean cancelled;
	private Boolean expired;
	
	@JoinColumn(nullable=true)
	@ManyToOne(fetch = FetchType.EAGER)
	private BonusRevision bonus;   //this should have been called bonusRevision..

//	@JsonIgnore
	@JsonBackReference("current")
	@JoinColumn(nullable=true)
	@ManyToOne(fetch = FetchType.EAGER)
	private PlayerBonus playerBonus;

	private Long customFreeMoneyAmountCents; // This is used in manual trigger for now
	private Long customBonusTokenAmountCents;
	private Long requestId;
	private String description;
	private String clientId;
	private Long sessionId;
	private String noteText;
}
