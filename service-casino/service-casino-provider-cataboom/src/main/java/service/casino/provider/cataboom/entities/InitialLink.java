package service.casino.provider.cataboom.entities;

import java.sql.Date;
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
@Table(indexes= {@Index(name="idx_playid",columnList="playid",unique=true)})
public class InitialLink {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="id")
	private Long id;	
	@NotNull
	@Column( name="link")
	private String link;
	@NotNull
	@Column( name="campaignid")
	private String campaignid;
	@NotNull
	@Column( name="timestamp")
	private Timestamp timestamp;
	@Column( name="playid")
	private String playid;
	
	@PrePersist
	public void prePersist() {
		timestamp=new Timestamp(System.currentTimeMillis());
	}
	
	   @ManyToOne(fetch = FetchType.LAZY)
	    @JoinColumn(name="user_id")
	   @JsonManagedReference
	    private User user;
}