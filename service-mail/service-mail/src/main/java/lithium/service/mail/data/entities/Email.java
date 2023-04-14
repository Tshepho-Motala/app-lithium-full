package lithium.service.mail.data.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;
import java.util.Date;

@Entity
@Data
@ToString(exclude = "template")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@JsonIdentityInfo(generator=ObjectIdGenerators.None.class, property="id")
@Table(indexes = {
	@Index(name="idx_created_date", columnList="createdDate", unique=false),
	@Index(name="idx_sent_date", columnList="sentDate", unique=false),
	@Index(name="idx_failed", columnList="failed", unique=false),
	@Index(name = "idx_f_pro_sd_ec_p_cd", columnList = "failed, processing, sentDate, errorCount, priority, createdDate")
})
public class Email {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
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
	
	@Column(nullable=true)
	private String bcc;
	
	@Column(nullable=false)
	private String subject;
	
	@Column(nullable=false, length=1000000)
	private String body;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(nullable=true)
	private User user;
	
	@Column(nullable=false)
	private Boolean processing = false;
	
	@Column(nullable=true)
	private Date processingStarted;
	
	@Column(nullable=false)
	private Integer errorCount = 0;
	
	@Column(nullable=true, length=1000000)
	private String latestErrorReason;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(nullable=false)
	private Domain domain;
	
	@Column(nullable=true)
	private String attachmentName;
	
	@Lob
	@Basic(fetch=FetchType.LAZY)
	private byte[] attachmentData;
	
	@Column(nullable=false)
	private Boolean failed = false;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(nullable=true)
	private User author;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(nullable=true)
	private EmailTemplate template;
	
	@Version
	private int version;
	
	@Transient
	private lithium.service.user.client.objects.User fullUser;

	public static class EmailBuilder {
		private Boolean processing = false;
		private Integer errorCount = 0;
		private Boolean failed = false;
	}
}