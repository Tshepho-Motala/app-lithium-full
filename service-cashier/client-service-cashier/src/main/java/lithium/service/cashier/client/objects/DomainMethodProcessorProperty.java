package lithium.service.cashier.client.objects;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import lithium.service.cashier.client.objects.transaction.dto.DomainMethodProcessor;
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
@JsonIdentityInfo(generator = ObjectIdGenerators.None.class, property = "id")
public class DomainMethodProcessorProperty implements Serializable {
	private static final long serialVersionUID = -5287126086639080535L;
	
	private Long id;
	private int version;
	
	private ProcessorProperty processorProperty;
	private DomainMethodProcessor domainMethodProcessor;
	
	private String value;
}