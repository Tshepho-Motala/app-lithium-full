package lithium.service.product.data.entities;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Version;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Entity
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder=true)
@Table(indexes = {
	@Index(name="idx_pg_product", columnList="product_id", unique=false),
	@Index(name="idx_pg_product_function", columnList="product_id, graphic_function_id", unique=false)
})
public class ProductGraphic implements Serializable {
	private static final long serialVersionUID = -3122612823596337599L;

	@Version
	private int version;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@Column(nullable=false)
	private boolean enabled;
	
	@OneToOne(cascade = CascadeType.MERGE)
	@JoinColumn(name = "product_id", nullable=false)
	private Product product;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(nullable=false)
	private Graphic graphic;
	
	@ManyToOne
	@JoinColumn(nullable=false)
	private GraphicFunction graphicFunction;
	
	private boolean deleted;
}
