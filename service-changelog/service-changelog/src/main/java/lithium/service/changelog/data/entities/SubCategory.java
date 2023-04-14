package lithium.service.changelog.data.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lithium.jpa.entity.EntityWithUniqueName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(indexes={
	@Index(name="idx_name", columnList="name", unique=true)
})
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class SubCategory implements EntityWithUniqueName {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;

	@Column(nullable=false)
	private String name;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(nullable=false, name="category_id")
	Category category;

	public SubCategory(Category category) {
		this.category = category;
	}
}
