package lithium.service.cashier.data.objects;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import lithium.service.cashier.client.objects.ProcessedProcessorProperty;
import lithium.service.cashier.data.entities.Fees;
import lithium.service.cashier.data.entities.Limits;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of={"processorId","domainMethodProcessorId","name","url"})
@JsonIdentityInfo(generator = ObjectIdGenerators.None.class, property = "id")
public class ProcessedProcessor implements Serializable {
	private static final long serialVersionUID = 6849836243363497258L;
	
	private Long processorId;
	private Long domainMethodProcessorId;
	private String name;
	private String url;
	
	private Double weight;
	
	private Fees fees;
	private Limits limits;
	
	private List<ProcessedProcessorProperty> properties;
}