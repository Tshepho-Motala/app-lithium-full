package lithium.service.event.entities;

import java.util.Date;

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
@JsonIdentityInfo(generator = ObjectIdGenerators.None.class, property = "id")
@Table(indexes = {
		@Index(name="idx_event_uniqueness", columnList="user_id, event_type_id, currency_id, duplicateEventPreventionKey", unique=true)
	})

public class Event {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@Version
	int version;

	@JoinColumn(nullable=false)
	@ManyToOne(fetch=FetchType.EAGER)
	private User user;
	
	@JoinColumn(nullable=false)
	@ManyToOne(fetch=FetchType.EAGER)
	private Domain domain;
	
	@JoinColumn(nullable=false)
	@ManyToOne(fetch=FetchType.EAGER)
	private EventType eventType;
	
	@JoinColumn(nullable=false)
	@ManyToOne(fetch=FetchType.EAGER)
	private Currency currency;
	
	private Date eventDate;
	
	private String duplicateEventPreventionKey; //dedup (used to identify previous events that means the current one should be discarded)
	
	@PrePersist
	public void defaults() {
		if (eventDate == null) eventDate = new Date();
	}
}
