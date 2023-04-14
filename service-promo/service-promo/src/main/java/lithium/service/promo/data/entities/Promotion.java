package lithium.service.promo.data.entities;

import java.io.Serial;
import java.io.Serializable;
import java.util.StringJoiner;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude="edit")
@Table(name = "promotion", indexes = {
		@Index(name="idx_enabled_deleted", columnList="deleted, enabled")
})
@Where(clause = "deleted=false")
public class Promotion implements Serializable {
	@Serial
	private static final long serialVersionUID = 7385108716219785497L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;

	@Version
	private int version;

	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(nullable=true)
	private User editor;

	@JsonManagedReference("promotion")
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(nullable=true, name = "current_id")
	private PromotionRevision current;

	@JsonManagedReference("promotion")
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(nullable=true, name = "edit_id")
	private PromotionRevision edit;

	@Builder.Default
	private Boolean enabled = Boolean.FALSE;

	@Builder.Default
	private Boolean deleted = Boolean.FALSE;

	@Override
	public String toString() {
		return new StringJoiner(", ", Promotion.class.getSimpleName() + "[", "]")
				.add("id=" + id)
				.add("editor=" + ((editor!=null)?editor.guid():"null"))
				.add("current=" + current)
				.add("edit=" + ((edit!=null)?edit.toShortString():"null"))
				.toString();
	}

	public String getCacheKey() {
		if (current != null) {
			return current.getDomain().getName();
		}

		return String.valueOf(serialVersionUID);
	}
}
