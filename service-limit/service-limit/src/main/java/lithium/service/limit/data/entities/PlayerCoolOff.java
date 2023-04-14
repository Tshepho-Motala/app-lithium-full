package lithium.service.limit.data.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.joda.time.DateTime;
import org.joda.time.Days;

import javax.persistence.Column;
import javax.persistence.Entity;
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
public class PlayerCoolOff implements Serializable {
	private static final long serialVersionUID = -5052505767900589031L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	@Version
	private int version;

	@Column(nullable=false)
	private String playerGuid;

	@Column(nullable=false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdDate;
	
	@Column(nullable=false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date expiryDate;
	
	@PrePersist
	public void prePersist() {
		createdDate = new Date();
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
	public Integer getPeriodInDays() {
		Days days = Days.daysBetween(new DateTime(this.createdDate).withTimeAtStartOfDay(), new DateTime(this.expiryDate).withTimeAtStartOfDay());
		return days.getDays();
	}
}