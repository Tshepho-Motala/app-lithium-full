package lithium.service.casino.cms.storage.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Version;
import java.io.Serializable;

@Entity
@Data
@ToString(exclude = "edit")
@Builder
@EqualsAndHashCode(exclude = "edit")
@AllArgsConstructor
@NoArgsConstructor
public class Lobby implements Serializable {
	private static final long serialVersionUID = 3014205353770661394L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Version
	private int version;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(nullable = false)
	private Domain domain;

	@JsonManagedReference("lobby")
	@ManyToOne
	@JoinColumn
	private LobbyRevision current;

	@JsonManagedReference("lobby")
	@ManyToOne
	@JoinColumn
	private LobbyRevision edit;
}
