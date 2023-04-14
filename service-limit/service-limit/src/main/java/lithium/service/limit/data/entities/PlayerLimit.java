package lithium.service.limit.data.entities;

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
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;
import java.io.Serializable;

@Data
@Entity
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Table(indexes = {
	@Index(name="idx_pl_player_gran_type", columnList="playerGuid, granularity, type", unique=true),
	@Index(name="idx_pl_player", columnList="playerGuid"),
	@Index(name="idx_pl_domain", columnList="domainName")
})
@Builder(toBuilder = true)
@EntityListeners(AuditingEntityListener.class)
public class PlayerLimit implements Serializable {
	private static final long serialVersionUID = -1;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	@Version
	private int version;

	@Column(nullable=false)
	private String playerGuid;
	
	@Column(nullable=false)
	private String domainName;

	@Column(nullable=false)
	private int granularity;
	
	@Column(nullable=false)
	private long amount;
	
	@Column(nullable=false)
	private int type;

	@Column(updatable = false)
	@CreatedDate
	private long createdDate;
	@LastModifiedDate
	private long modifiedDate;

	@Transient
	private long amountUsed;
}