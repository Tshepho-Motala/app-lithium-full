package lithium.service.report.games.data.entities;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Version;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Data
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(indexes={
		@Index(name="idx_internal_id", columnList="internalId"),
})
@JsonIgnoreProperties(value={"reportRun"})
public class ReportRunResults implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Version
	int version;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	long id;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(nullable=true)
	ReportRun reportRun;
	
	@ManyToOne(fetch = FetchType.EAGER) @JoinColumn(nullable=true) 
	StringValue name;
	
	Long internalId;
	
	@ManyToOne(fetch = FetchType.EAGER) @JoinColumn(nullable=true) 
	StringValue providerId;
	
	@ManyToOne(fetch = FetchType.EAGER) @JoinColumn(nullable=true) 
	StringValue providerName;
	
	@ManyToOne(fetch = FetchType.EAGER) @JoinColumn(nullable=true)
	StringValue enabled;
	
	Long casinoBetAmountCents;
	Long casinoBetCount;
	Long casinoWinAmountCents;
	Long casinoWinCount;
	Long casinoNetAmountCents;
	
	Long casinoBonusBetAmountCents;
	Long casinoBonusBetCount;
	Long casinoBonusWinAmountCents;
	Long casinoBonusWinCount;
	Long casinoBonusNetAmountCents;
	
	@PrePersist
	void defaults() {
	}
}