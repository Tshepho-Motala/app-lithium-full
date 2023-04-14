package lithium.service.cashier.client.objects;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties
@JsonIdentityInfo(generator = ObjectIdGenerators.None.class, property = "id")
public class ProcessedProcessorProperty implements Serializable {
	private static final long serialVersionUID = -8569102860668062597L;

	private Long id;
	
	private String name;
	
	private String value;
	
	private String type;

	private String description;
}