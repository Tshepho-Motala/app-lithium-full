package lithium.service.pushmsg.data.entities;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Version;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonManagedReference;
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
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@JsonIdentityInfo(generator=ObjectIdGenerators.None.class, property="id")
@Table(indexes = {
	@Index(name="idx_pushmsg_created_date_priority", columnList="createdDate, priority", unique=false),
	@Index(name="idx_pushmsg_sent_date_priority", columnList="sentDate, priority", unique=false)
})
public class PushMsg implements Serializable {
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
	
//	@Column(nullable=true)
//	@OneToMany(fetch=FetchType.EAGER, mappedBy="provider", cascade=CascadeType.ALL)
//	private List<ProviderProperty> properties = new ArrayList<>();
	@ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
	@JoinTable(
		name = "included_segments", 
		joinColumns = {	@JoinColumn(name = "pushmsg_id", nullable = false, updatable = false) }, 
		inverseJoinColumns = { @JoinColumn(name = "segment_id", nullable = false, updatable = false) },
		indexes = { 
			@Index(name="idx_urr_id", columnList="pushmsg_id,segment_id", unique=true)
		}
	)
	@JsonManagedReference("included_segments")
	private List<Segment> includedSegments;
	
	@ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
	@JoinTable(
		name = "excluded_segments", 
		joinColumns = {	@JoinColumn(name = "pushmsg_id", nullable = false, updatable = false) }, 
		inverseJoinColumns = { @JoinColumn(name = "segment_id", nullable = false, updatable = false) },
		indexes = { 
			@Index(name="idx_urr_id", columnList="pushmsg_id,segment_id", unique=true)
		}
	)
	@JsonManagedReference("excluded_segments")
	private List<Segment> excludedSegments;
	
	private String sendAfter;
	private String delayedOption;
	private String deliveryTimeOfDay;
	private String ttl;
	@Column(nullable=false)
	private Integer priority;
	
	private Boolean isIos;
	private Boolean isAndroid;
	private Boolean isAnyWeb;
	private Boolean isChromeWeb;
	private Boolean isFirefox;
	private Boolean isSafari;
	private Boolean isWP_WNS;
	private Boolean isAdm;
	private Boolean isChrome;
	
	@Column(nullable=true)
	private String templateId;
	
	@OneToMany(fetch=FetchType.EAGER)
	@JoinColumn(nullable=true)
	private List<PushMsgHeading> pushMsgHeadings;
	@OneToMany(fetch=FetchType.EAGER)
	@JoinColumn(nullable=true)
	private List<PushMsgContent> pushMsgContents;
	
	@Column(nullable=true, length=1000000)
	private String latestErrorReason;
	
	@ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
	@JoinTable(
		name = "push_msg_users", 
		joinColumns = {	@JoinColumn(name = "pushmsg_id", nullable = false, updatable = false) }, 
		inverseJoinColumns = { @JoinColumn(name = "user_id", nullable = false, updatable = false) },
		indexes = { 
			@Index(name="idx_urr_id", columnList="pushmsg_id,user_id", unique=true)
		}
	)
	@JsonManagedReference("pushmsg_users")
	private List<User> users;
	
//	@Column(nullable=true)
//	private List<String> includePlayerIds;
//	@Column(nullable=true)
//	private List<String> includeExternalUserIds;
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(nullable=false)
	private Domain domain;
	
	@Column(nullable=true)
	private String providerReference;
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(nullable=true)
	private DomainProvider domainProvider;
	
	@Builder.Default
	@Column(nullable=false)
	private Boolean failed = false;
	
	@PrePersist
	private void prePersist() {
		if (createdDate == null) createdDate = new Date();
		if (priority == null) priority = 1;
		if (failed == null) failed = false;
	}
}