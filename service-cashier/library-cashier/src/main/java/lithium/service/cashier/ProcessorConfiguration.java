//package lithium.service.cashier;
//
//import java.math.BigDecimal;
//import java.util.ArrayList;
//
//import javax.validation.constraints.NotNull;
//
//import org.hibernate.validator.constraints.NotEmpty;
//import org.springframework.boot.context.properties.ConfigurationProperties;
//import org.springframework.stereotype.Component;
//
//import lombok.AllArgsConstructor;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//
//@Data
//@ConfigurationProperties(prefix="lithium.service.cashier.processor")
//public class ProcessorConfiguration {
//	@NotNull
//	private Boolean enabled;
//	@NotEmpty
//	private String name;
//	@NotEmpty
//	private String url;
//	@NotNull
//	private Fees fees;
//	@NotNull
//	private String[] methods;
//	@NotNull
//	private Limits limits;
////	private Double firstDepositWeight;
//	private ArrayList<Property> properties = new ArrayList<Property>();
//	
//	public Property findProperty(String name) {
//		for (Property p: properties) {
//			if (p.getName().equals(name)) { return p; }
//		}
//		return null;
//	}
//	
//	public String propertyValue(String name) {
//		Property p = findProperty(name);
//		if (p == null) return null;
//		return p.getDefaultValue();
//	}
//	
//	@Data
//	@NoArgsConstructor
//	@AllArgsConstructor
//	public static class Property {
//		private String name;
//		private String description;
//		private String defaultValue;
//		private String type;
//	}
//	
//	@Data
//	@NoArgsConstructor
//	@AllArgsConstructor
//	public static class Fees {
//		private Long flat;
//		private BigDecimal percentage;
//		private Long minimum;
//	}
//	
//	@Data
//	@NoArgsConstructor
//	@AllArgsConstructor
//	public static class Limits {
//		private Long minAmount;
//		private Long maxAmount;
//		private Long maxAmountDay;
//		private Long maxAmountWeek;
//		private Long maxAmountMonth;
//		
//		private Long maxTransactionsDay;
//		private Long maxTransactionsWeek;
//		private Long maxTransactionsMonth;
//	}
//}
