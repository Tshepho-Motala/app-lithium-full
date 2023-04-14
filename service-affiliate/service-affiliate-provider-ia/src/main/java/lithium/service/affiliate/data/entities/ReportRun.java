package lithium.service.affiliate.data.entities;

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
import javax.persistence.Version;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang.RandomStringUtils;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Data
@ToString(exclude={"report"})
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(value={"report","reportRevision"})
@Table(indexes={
		@Index(name="idx_accesskey", columnList="accessKey", unique=true),
		@Index(name="idx_started", columnList="startedOn", unique=false),
		@Index(name="idx_completed", columnList="completedOn", unique=false)
})
public class ReportRun implements Serializable {

	private static final long serialVersionUID = 1L;

	@Version
	int version;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	long id;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(nullable=false)
	ReportRevision reportRevision;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(nullable=false)
	Report report;

	Boolean completed;
	Boolean failed;
	
	@Column(nullable=true, length=10000)
	String failReason;
	
	@Column(nullable=false)
	Date startedOn;

	@Column(nullable=true)
	Date completedOn;

	@Column(nullable=false)
	String startedBy;
	
	@Column(nullable=false)
	String accessKey;

	@Column(nullable=true)
	Long totalRecords;
	
	@Column(nullable=true)
	Long filteredRecords;
	
	@Column(nullable=true)
	Long processedRecords;
	
	@Column(nullable=true)
	Long actionsPerformed;

	@Column(nullable=true)
	Date lastUpdate;

	@Column(nullable=true)
	Date periodStartDate;

	@PrePersist
	void defaults() {
		lastUpdate = new Date();
		if (accessKey == null) accessKey = Hex.encodeHexString(RandomStringUtils.randomAlphanumeric(40).getBytes());
	}

}
