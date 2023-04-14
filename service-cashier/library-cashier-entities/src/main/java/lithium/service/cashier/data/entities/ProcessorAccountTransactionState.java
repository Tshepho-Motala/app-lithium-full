package lithium.service.cashier.data.entities;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.persistence.Version;
import lithium.jpa.entity.EntityWithUniqueName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Entity(name = "cashier.ProcessorAccountTransactionState")
@Builder
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Table(
	catalog = "lithium_cashier",
	name = "processor_account_transaction_state",
	indexes = {
		@Index(name="idx_name", columnList="name", unique=true)
	})
public class ProcessorAccountTransactionState implements EntityWithUniqueName, Serializable {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer id;

	@Version
	private int version;

	@Column(nullable=false)
	private String name;
}
