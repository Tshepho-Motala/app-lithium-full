package service.casino.provider.cataboom.entities;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Data
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(indexes= {@Index(name="idx_collection",columnList="user_id,playcode",unique=true)})
public class PrizeFullfilment {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="id")
	private Long id;	
	
	@JoinColumn(name="initial_link_id", nullable=true)
	@OneToOne(optional=true)
	private InitialLink initialLink;
	
	@Column( name="description", nullable=true)
	private String description;
	
	@Column( name="prizepin", nullable=true)
	private String prizepin;
	
	@NotNull
	@Column( name="token")
	private String token;
	
	@NotNull
	@Column( name="campaignid")
	private String campaignid;
	
	@NotNull
	@Column( name="playerid")
	private String playerid;
	
	@Column( name="prizecode", nullable=true)
	private String prizecode;
	
	@NotNull
	@Column( name="winlevel")
	private String winlevel;
	
	@Column( name="prizelink", nullable=true)
	private String prizelink;
	
	@NotNull
	@Column( name="playcode")
	private String playcode;
	
	@NotNull
	@Column( name="timestamp")
	private Timestamp timestamp;

	@PrePersist
	public void prePersist() {
		timestamp=new Timestamp(System.currentTimeMillis());
	}
	
	 @ManyToOne(fetch = FetchType.LAZY)
	    @JoinColumn(name="user_id")
	   @JsonManagedReference
	    private User user;
	
	
}