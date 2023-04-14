package lithium.service.sms.data.entities;

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

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@JsonIdentityInfo(generator=ObjectIdGenerators.None.class, property="id")
@Table(indexes = {
	@Index(name="idx_sms_created_date_priority", columnList="createdDate, priority", unique=false),
	@Index(name="idx_sms_sent_date_priority", columnList="sentDate, priority", unique=false)
})
public class SMS implements Serializable {
	private static final long serialVersionUID = 1401640959223983847L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	@Version
	private int version;
	
	@Column(nullable=false)
	private Date createdDate;
	
	@Column(nullable=true)
	private Date sentDate;
	
	@Column(nullable=false)
	private Integer priority;
	
	@Column(name="`from`", nullable=false)
	private String from;
	
	@Column(name="`to`", nullable=false)
	private String to;
	
	@Column(nullable=false, length=1000000)
	private String text;
	
	@Column(nullable=false)
	private Boolean processing;
	
	@Column(nullable=true)
	private Date processingStarted;
	
	@Column(nullable=false)
	private Integer errorCount;
	
	@Column(nullable=true, length=1000000)
	private String latestErrorReason;
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(nullable=true)
	private User user;
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(nullable=false)
	private Domain domain;
	
	@Column(nullable=true)
	private String providerReference;
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(nullable=true)
	private DomainProvider domainProvider;
	
	@Column(nullable=true)
	private Date receivedDate;
	
	@Column(nullable=false)
	private Boolean failed = false;
	
	@Transient
	private lithium.service.user.client.objects.User fullUser;
	
	@PrePersist
	private void prePersist() {
		if (createdDate == null) createdDate = new Date();
		if (priority == null) priority = 1;
		if (processing == null) processing = false;
		if (errorCount == null) errorCount = 0;
		if (failed == null) failed = false;
	}
}