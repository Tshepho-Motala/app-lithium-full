package lithium.service.cashier.client.objects;

import java.io.Serializable;

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
@JsonIdentityInfo(generator = ObjectIdGenerators.None.class, property = "id")
public class ProcessorProperty implements Serializable {
	private static final long serialVersionUID = 1654465444811265735L;
	
	private Long id;
	private int version;
	private String name;
	private String defaultValue;
	private String type;
	private String description;
	private boolean avalableForClient;
}
