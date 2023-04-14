package lithium.service.report.games.data.entities;

import java.io.Serializable;
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
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Data
@ToString(exclude={"running","lastCompleted", "lastFailed"})
@Table(indexes = {
		@Index(name="idx_domain", columnList="domainName", unique=false),
		@Index(name="idx_enabled", columnList="enabled", unique=false),
		@Index(name="idx_deleted", columnList="deleted", unique=false),
		@Index(name="idx_created_by", columnList="createdBy", unique=false)
	})
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Report implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Version
	int version;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	long id;
	
	@Column(nullable=false)
	String domainName;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(nullable=true)
	ReportRevision current;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(nullable=true)
	ReportRevision edit;
	
	@Column(nullable=true)
	String editor;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(nullable=true)
	ReportRun running;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(nullable=true)
	ReportRun lastCompleted;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(nullable=true)
	ReportRun lastFailed;
	
	@JoinColumn(nullable=true)
	Long runRetriesCount;
	
	Boolean enabled;
	
	Boolean deleted;
	
	@Column(nullable=true)
	Date scheduledDate;
	
	@Transient
	public Date getLastCompletedDate() {
		if (lastCompleted != null) return lastCompleted.getCompletedOn();
		return null;
	}
	
	@Transient
	public Date getLastFailedDate() {
		if (lastFailed != null) return lastFailed.getCompletedOn();
		return null;
	}
	
	@Transient
	public Date getRunningSince() {
		if (running != null) return running.getStartedOn();
		return null;
	}
	
	@Column(nullable=false)
	@DateTimeFormat(iso=ISO.DATE_TIME)
	Date createdDate;
	
	@Column(nullable=false)
	String createdBy;
	
	@PrePersist
	void defaults() {
		if (enabled == null) enabled = false;
		if (deleted == null) deleted = false;
		if ((current != null) && (current.cron == null || current.cron.isEmpty())) scheduledDate = new Date();
		if (createdDate == null) createdDate = new Date();
	}
}