package lithium.service.limit.data.entities;

import lithium.service.limit.converter.EnumConverter;
import lithium.service.limit.enums.ModifyType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.Convert;
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
import javax.persistence.ManyToOne;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.Version;
import javax.persistence.EntityListeners;
import java.io.Serializable;
import java.util.Date;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@Table(indexes = {
	@Index(name="idx_plt_player_time", columnList="playerGuid", unique=true)
})

@Builder(toBuilder = true)
@EntityListeners(AuditingEntityListener.class)
public class PlayerTimeSlotLimit implements Serializable {

	private static final long serialVersionUID = -1;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;

	@Version
	private int version;

	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(nullable=false)
	private Domain domain;

	@Column(nullable=false)
	private long limitFromUtc;

	@Column(nullable=false)
	private long limitToUtc;

	@Column(nullable=false)
	private String playerGuid;

	@Column(updatable = false)
	@CreatedDate
	private Date createTimestamp;

	@Column(nullable = false)
	@LastModifiedDate
	private Date modifyTimestamp = new Date();

	@PrePersist
	private void prePersist() {
		if (createTimestamp == null) createTimestamp = new Date();
	}
}
