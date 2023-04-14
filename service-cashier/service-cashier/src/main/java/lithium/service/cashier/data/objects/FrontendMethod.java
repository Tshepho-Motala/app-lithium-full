package lithium.service.cashier.data.objects;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lithium.service.cashier.data.entities.Domain;
import lithium.service.cashier.data.entities.DomainMethodProcessorProperty;
import lithium.service.cashier.data.entities.Fees;
import lithium.service.cashier.data.entities.Image;
import lithium.service.cashier.data.entities.Limits;
import lithium.service.cashier.data.views.Views;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of={"methodId","domainMethodId","domain"})
@JsonIdentityInfo(generator = ObjectIdGenerators.None.class, property = "id")
public class FrontendMethod implements Serializable {
	private static final long serialVersionUID = -5695671474761183182L;
	
	private Long methodId;
	private String methodCode;
	private Long domainMethodId;
	private String name;
	@JsonProperty("default")
	private Boolean feDefault;
	private Integer priority;
	
	private boolean deposit;
	
	@Default
	private Boolean inApp = false;
	
	private String platform; //apple/google/null
	
	private Domain domain;
	
	private Fees fees;
	private Limits limits;
	
	private boolean blockProcessing;
	private Long userBalanceCents;
	
	@JsonView(Views.Image.class)
	private Image image;
	
	private List<DomainMethodProcessorProperty> properties;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	private List<String> allowCardTypes;

}