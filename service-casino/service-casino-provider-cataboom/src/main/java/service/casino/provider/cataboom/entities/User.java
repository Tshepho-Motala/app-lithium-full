package service.casino.provider.cataboom.entities;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Data
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user", indexes= {@Index(name="player_guid",columnList="player_guid",unique=true)})
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private Long id;
	@NotNull
	@Column(name = "player_guid")
	private String playerGuid;

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
	 @JsonBackReference
	private List<InitialLink> initialLinks;
	
	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
	 @JsonBackReference
	private List<PrizeFullfilment> prizeFullfilments;
}