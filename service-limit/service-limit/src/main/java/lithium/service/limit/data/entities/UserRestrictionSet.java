package lithium.service.limit.data.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Transient;
import javax.persistence.Table;
import javax.persistence.Version;
import java.text.SimpleDateFormat;
import java.util.Date;

@Data
@Entity
@Builder
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Table(indexes={
	@Index(name="idx_user_set", columnList="user_id, set_id", unique=true)
})
public class UserRestrictionSet {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;

	@Version
	private int version;

	@ManyToOne
	@JoinColumn(nullable=false)
	private User user;

	private Date createdOn;
	private Date activeFrom;
	private Date activeTo;

	@ManyToOne
	@JoinColumn(nullable=false)
	private DomainRestrictionSet set;

	@Transient
	public String getCreatedDateDisplay() {
		if (createdOn != null) {
			return new SimpleDateFormat("MMM dd, yyyy HH:mm").format(createdOn);
		}
		return "";
	}

	@Transient
	public String getActiveFromDisplay() {
		if (activeFrom != null) {
			return new SimpleDateFormat("MMM dd, yyyy HH:mm").format(activeFrom);
		}
		return "";
	}

	@Transient
	public String getActiveToDisplay() {
		if (activeTo != null) {
			return new SimpleDateFormat("MMM dd, yyyy HH:mm").format(activeTo);
		}
		return "";
	}

	private Integer subType;
}
