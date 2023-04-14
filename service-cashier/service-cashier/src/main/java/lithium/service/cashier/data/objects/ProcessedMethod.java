package lithium.service.cashier.data.objects;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import lithium.service.cashier.data.entities.Domain;
import lithium.service.cashier.data.entities.Image;
import lithium.service.cashier.data.views.Views;
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
@EqualsAndHashCode(of={"methodId","domainMethodId","domain"})
@JsonIdentityInfo(generator = ObjectIdGenerators.None.class, property = "id")
public class ProcessedMethod implements Serializable {
	private static final long serialVersionUID = 2309793138941083148L;
	
	private Long methodId;
	private Long domainMethodId;
	private String name;
	
	private Integer priority;
	
	private Domain domain;
	
	@JsonView(Views.Image.class)
	private Image image;
	
	@JsonView(Views.ProcessedProcessor.class)
	private List<ProcessedProcessor> processors;
}