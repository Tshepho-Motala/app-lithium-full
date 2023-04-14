package lithium.service.casino.cms.storage.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
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
import javax.persistence.Lob;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Version;
import javax.persistence.EntityListeners;
import java.io.Serializable;
import java.util.Date;

@Entity
@Data
@ToString(exclude = "lobby")
@Builder
@EqualsAndHashCode(exclude = "lobby")
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class LobbyRevision implements Serializable {
	private static final long serialVersionUID = -6642928783250255663L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Version
	private int version;

	@JsonBackReference("lobby")
	@ManyToOne
	@JoinColumn(nullable = false)
	private Lobby lobby;

	@Column(nullable = false, length = 2000)
	private String description;

	@Column(nullable = false, updatable = false)
	@CreatedDate
	private Date createdDate;

	@ManyToOne
	@JoinColumn(nullable = false)
	private User createdBy;

	@Column
	@LastModifiedDate
	private Date modifiedDate;

	@ManyToOne
	@JoinColumn
	private User modifiedBy;

	@Lob
	@Column(nullable = false)
	private String json;
}
