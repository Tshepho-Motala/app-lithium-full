package lithium.service.cashier.data.objects;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import lithium.service.cashier.data.entities.Domain;
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
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@JsonIdentityInfo(generator = ObjectIdGenerators.None.class, property = "id")
public class ProcessedDomainMethodProcessor {
	
	private String description;
	private ProcessedProcessor processor;
	private ProcessedDomainMethod domainMethod;
	private Double weight;
	private Fees fees;
	private Limits limits;
	
	@Data
	@Builder
	@ToString
	@EqualsAndHashCode
	@NoArgsConstructor
	@AllArgsConstructor
	@JsonIdentityInfo(generator = ObjectIdGenerators.None.class, property = "id")
	public static class ProcessedDomainMethod {
		private Long id;
		private String name;
		private String code;
		private Domain domain;
		private Boolean deposit;
		private Integer priority;
	}
	
	@Data
	@Builder
	@ToString
	@EqualsAndHashCode
	@NoArgsConstructor
	@AllArgsConstructor
	@JsonIdentityInfo(generator = ObjectIdGenerators.None.class, property = "id")
	public static class ProcessedProcessor {
		private String code;
		private Boolean deposit;
		private Boolean withdraw;
		private String name;
		private String url;
		private List<ProcessedProperty> properties;
		
		@Data
		@ToString
		@EqualsAndHashCode
		@NoArgsConstructor
		@AllArgsConstructor
		@JsonIdentityInfo(generator = ObjectIdGenerators.None.class, property = "id")
		public class ProcessedProperty {
			private String name;
			private String value;
			private String type;
			private String description;
		}
	}
}
