package lithium.service.casino.data.objects;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class FrontendWinner implements Serializable {
	private static final long serialVersionUID = -1L;
	
	private String domainName;
	
	private Long amount;
	
	private String gameName;
	
	private String firstName;
	
	private Date createdDate;
	
	private String guid;
}