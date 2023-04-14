package lithium.service.report.players.data.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Version;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Data
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(value={"reportRevision"})
public class ReportFilter implements Serializable {

	private static final long serialVersionUID = 1L;

	@Version
	int version;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	long id;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(nullable=false)
	ReportRevision reportRevision;

	@Column(nullable=false)
	String field;
	
	@Column(nullable=false)
	String operator;

	@Column(nullable=true)
	String value;

}
