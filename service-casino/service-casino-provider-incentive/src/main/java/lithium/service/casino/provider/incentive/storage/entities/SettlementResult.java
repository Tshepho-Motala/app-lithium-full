package lithium.service.casino.provider.incentive.storage.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lithium.jpa.entity.EntityWithUniqueCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.persistence.Version;

@Entity
@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(indexes = {
		@Index(name="idx_code", columnList="code", unique=true),
	})
@EqualsAndHashCode
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class SettlementResult implements EntityWithUniqueCode {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@Version
	int version;

	@Column(nullable=false)
	private String code;
}
