package lithium.service.casino.data.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.joda.time.DateTime;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;
import java.io.Serializable;
import java.util.Date;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude={"file"})
public class BonusFileUpload implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	@Version
	private int version;

	@Lob
	private byte[] file;
	
	private long size;
	
	private String fileType;

	private boolean complete;

	private boolean hadSomeErrors; //If the job was executed but there were some errors in the individual records

	@ManyToOne
	@JoinColumn(nullable=false)
	private User author;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(nullable=false)
	private BonusRevision bonusRevision;

	@Temporal(TemporalType.TIMESTAMP)
	private Date creationDate;

	@Temporal(TemporalType.TIMESTAMP)
	private Date completionDate;

	@PrePersist
	public void prePersist() {
		if (this.creationDate == null) this.creationDate = DateTime.now().toDate();
	}
}
