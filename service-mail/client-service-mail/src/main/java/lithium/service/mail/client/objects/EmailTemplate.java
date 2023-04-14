package lithium.service.mail.client.objects;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class EmailTemplate implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;
	
	int version;

	private String lang;
	
	private String name;

//	private EmailTemplateRevision edit;
//	
//	private Date editStartedOn;
//	
//	private User editBy;
//
//	private EmailTemplateRevision current;
//	
//	private Domain domain;
//
//	private EmailType type;

}
