package lithium.service.report.players.data.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

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
import javax.persistence.Version;
import java.io.Serializable;
import java.util.Date;

@Entity
@Data
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(indexes={
	@Index(name="idx_created", columnList="createdDate"),
	@Index(name="idx_dob", columnList="dateOfBirth"),
	@Index(name="idx_dob_day", columnList="dateOfBirthDay"),
	@Index(name="idx_dob_month", columnList="dateOfBirthMonth"),
	@Index(name="idx_dob_year", columnList="dateOfBirthYear"),
})
@JsonIgnoreProperties(value={"reportRun"})
public class ReportRunResults implements Serializable {

	private static final long serialVersionUID = 1L;

	@Version
	int version;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	long id;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(nullable=true)
	ReportRun reportRun;
	
	@ManyToOne(fetch = FetchType.EAGER) @JoinColumn(nullable=true) 
	StringValue username;
	
	@ManyToOne(fetch = FetchType.EAGER) @JoinColumn(nullable=true) 
	StringValue email;
	
	@ManyToOne(fetch = FetchType.EAGER) @JoinColumn(nullable=true) 
	StringValue firstName;
	
	@ManyToOne(fetch = FetchType.EAGER) @JoinColumn(nullable=true) 
	StringValue lastName;
	
	@ManyToOne(fetch = FetchType.EAGER) @JoinColumn(nullable=true) 
	StringValue cellphoneNumber;

	@ManyToOne(fetch = FetchType.EAGER) @JoinColumn(nullable=true)
	StringValue gender;

	@ManyToOne(fetch = FetchType.EAGER) @JoinColumn(nullable=true)
	StringValue stage;
	
	Date createdDate;
	
	Date dateOfBirth;
	Integer dateOfBirthDay;
	Integer dateOfBirthMonth;
	Integer dateOfBirthYear;
	
	Long userId;
	
	@PrePersist
	void defaults() {
	}

}
