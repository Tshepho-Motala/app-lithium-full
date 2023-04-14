package lithium.service.cashier.data.entities;

import lithium.jpa.entity.EntityWithUniqueName;
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
import java.io.Serializable;

@Data
@Entity(name = "cashier.TransactionRemarkType")
@Builder
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Table(
	catalog = "lithium_cashier",
	name = "transaction_remark_type",
	indexes = {
		@Index(name="idx_name", columnList="name", unique=true)
	})
public class TransactionRemarkType implements EntityWithUniqueName, Serializable {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer id;

	@Version
	private int version;

	@Column(nullable=false)
	private String name;
}
