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

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
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
@JsonIdentityInfo(generator=ObjectIdGenerators.None.class, property="id")
@Table(indexes = {
	@Index(name="idx_domain_token", columnList="domain_id, token", unique=true)
})
public class AutoBonusAllocation {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(nullable=false)
	private Domain domain;
	
	@Column(nullable=false)
	private String token;
	
	@Column(nullable=false)
	@DateTimeFormat(iso=ISO.DATE_TIME)
	private Date requestDate;
	
	@Column(nullable=false)
	private String requestIp;
	
	@Column(nullable=false)
	private String requestedBy;
	
	@Column(nullable=false)
	private String playerGuid;
	
	@Column(nullable=false)
	private Long bonusType; // 1 = deposit, 0 = signup
	
	@Column(nullable=false)
	private Long bonusId;
	
	@Column(nullable=true)
	private Long amountCents;
	
	@Column(nullable=true)
	private Long userEventId;
}
