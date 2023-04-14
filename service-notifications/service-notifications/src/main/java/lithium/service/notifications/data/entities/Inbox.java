package lithium.service.notifications.data.entities;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
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
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import lithium.service.notifications.client.objects.LabelValue;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

@Entity
@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Table(indexes = {
		@Index(name="idx_domain", columnList="domain_id", unique=false),
		@Index(name="idx_user", columnList="user_id", unique=false),
		@Index(name="idx_created_date", columnList="createdDate", unique=false),
		@Index(name="idx_sent_date", columnList="sentDate", unique=false),
		@Index(name="idx_read_date", columnList="readDate", unique=false),
		@Index(name="idx_read", columnList="read", unique=false),
		@Index(name = "idx_user_domain_notification_read", columnList = "user_id, domain_id, notification_id, read", unique = false),
		@Index(name = "idx_user_domain_read", columnList = "user_id, domain_id, read", unique = false)
})
public class Inbox implements Serializable {
	@Serial
	private static final long serialVersionUID = -238686872706126149L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;

	@Version
	private int version;

	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(nullable=false)
	private Domain domain;

	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(nullable=false)
	private User user;

	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(nullable=false)
	private Notification notification;

	@Column(nullable=false)
	private Date createdDate;

	@Column(nullable=false)
	private Date sentDate;

	@Column(name="`read`", nullable=false)
	private Boolean read;

	@Column(nullable=true)
	private Date readDate;

	@Column(nullable=true)
	private Date lastReadDate;

	@Column(nullable=false, length=1000000)
	private String message;

	@Transient
	private lithium.service.user.client.objects.User fullUser;

	@Column(nullable=false)
	private Boolean processing;

	@Column(nullable=false)
	private Boolean processed;

	@Column(nullable=false)
	private Boolean cta;

	@OneToMany(fetch=FetchType.EAGER, mappedBy="inbox", cascade=CascadeType.ALL)
	@JsonManagedReference("inbox")
	@Fetch(FetchMode.SELECT)
	private List<InboxMessagePlaceholderReplacement> phReplacements;

	@OneToMany(fetch=FetchType.EAGER, mappedBy="inbox", cascade=CascadeType.ALL)
	@JsonManagedReference("inbox")
	@Fetch( FetchMode.SELECT)
	private List<InboxLabelValue> metaData;

	@PrePersist
	public void prePersist() {
		if (createdDate == null) createdDate = new Date();
		if (sentDate == null) sentDate = new Date();
		if (read == null) read = false;
		if (lastReadDate == null && readDate != null) lastReadDate = readDate;
		if (processing == null) processing = false;
		if (processed == null) processed = false;
		if (cta == null) {
			cta = false;
		}
	}
}
