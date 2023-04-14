package lithium.service.access.data.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import java.util.Date;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Table(indexes={
	@Index(name="idx_user_external_list", columnList="user_id, external_list_id", unique=true)
})
public class UserExternalListValidation {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;

	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(nullable=false)
	private User user;

	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(nullable=false)
	private ExternalList externalList;

	@Column(nullable=false)
	@DateTimeFormat(iso=DateTimeFormat.ISO.DATE_TIME)
	private Date validatedOn;

	@Column(nullable=false)
	@DateTimeFormat(iso=DateTimeFormat.ISO.DATE_TIME)
	private Date updatedOn;

	@Column(nullable=false)
	private Boolean passed;

	@Column(nullable=true)
	private String message;

	@Column(nullable=true)
	private String errorMessage;

	@PrePersist
	private void prePersist() {
		if (validatedOn == null) validatedOn = new Date();
		if (updatedOn == null) updatedOn = new Date();
	}
}
