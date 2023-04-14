package lithium.service.casino.data.entities;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude={"edit"})
@EqualsAndHashCode(exclude="edit")
@JsonIdentityInfo(generator = ObjectIdGenerators.None.class, property = "id")
public class Bonus implements Serializable {
	private static final long serialVersionUID = 8622339402837301807L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	private String editUser;
	
	@JsonManagedReference("bonus")
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(nullable=true)
	private BonusRevision current;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(nullable=true)
	@JsonManagedReference("bonus")
	private BonusRevision edit;
}