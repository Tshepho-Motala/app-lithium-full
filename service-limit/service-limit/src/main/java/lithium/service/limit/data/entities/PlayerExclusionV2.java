package lithium.service.limit.data.entities;

import lithium.service.limit.client.objects.ExclusionSource;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.joda.time.DateTime;
import org.joda.time.Months;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.Version;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@Table(indexes = {
	@Index(name="idx_pe_player", columnList="playerGuid", unique=true),
	@Index(name="idx_pe_expiry_date", columnList="expiryDate", unique=false)
})
public class PlayerExclusionV2 implements Serializable {
	
	private static final long serialVersionUID = -1;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	@Version
	private int version;

	@Column(nullable=false)
	private String playerGuid;

	@Column(nullable=false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdDate;
	
	@Column(nullable=true)
	@Temporal(TemporalType.TIMESTAMP)
	private Date expiryDate;

	@Column(nullable=false)
	private boolean permanent;

	private String advisor;

	@Enumerated(EnumType.STRING)
	private ExclusionSource exclusionSource;
	
	@PrePersist
	public void prePersist() {
		if(createdDate==null) {
			createdDate = new Date();
		}
		if (permanent == true) {
			expiryDate = null;
		}
	}

	@Transient
	public String getCreatedDateDisplay() {
		if (createdDate != null) return new SimpleDateFormat("MMM dd, yyyy HH:mm").format(createdDate);
		return "";
	}

	@Transient
	public String getExpiryDateDisplay() {
		if (expiryDate != null) return new SimpleDateFormat("MMM dd, yyyy HH:mm").format(expiryDate);
		return "";
	}

	@Transient
	public Integer getPeriodInMonths() {
		if (this.expiryDate == null) return null;
		Months months = Months.monthsBetween(new DateTime(this.createdDate).withTimeAtStartOfDay(), new DateTime(this.expiryDate).withTimeAtStartOfDay());
		return months.getMonths();
	}
}
