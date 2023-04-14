package lithium.service.domain.data.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Version;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class BankingDetails implements Serializable {
	private static final long serialVersionUID = -2666452849619665438L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@Version
	private int version;
	
	@Column(nullable=true)
	private String orgId; // id provided by bank
	
	@Column(nullable=false)
	private String bankIdentifierCode;
	
	@Column(nullable=false)
	private String accountHolder;
	
	@Column(nullable=false)
	private String accountNumber;
}
