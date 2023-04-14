//package lithium.service.cashier;
//
//import java.util.Map;
//
//import org.hibernate.validator.constraints.NotEmpty;
//import org.springframework.boot.context.properties.ConfigurationProperties;
//
//import lombok.AllArgsConstructor;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//import lombok.ToString;
//
//@Data
//@ConfigurationProperties(prefix="lithium.service.cashier")
//@ToString
//public class MethodsConfiguration {
//	
//	private Map<String, Method> methods;
//	
//	@Data
//	@NoArgsConstructor
//	@AllArgsConstructor
//	public static class Method {
//		@NotEmpty private String name;
//		@NotEmpty private String url;
//		private Image image;
//		private Boolean hasAmount = true;
//		private Stage[] depositStages;
//		private Stage[] withdrawalStages;
//	}
//
//	@Data
//	@NoArgsConstructor
//	@AllArgsConstructor
//	public static class Image {
//		private String name;
//		private String url;
//	}
//	
//	@Data
//	@NoArgsConstructor
//	@AllArgsConstructor
//	public static class Stage {
//		private Integer number;
//		private String title;
//		private String description;
////		private RecordConfig recordConfig;
//		private Field[] outputFields;
//		private Field[] inputFields;
//	}
//	
////	@Data
////	@NoArgsConstructor
////	@AllArgsConstructor
////	public static class RecordConfig {
////		private String nameInline;
////		private String nameHeader;
////		private Field[] fields;
////	}
//	
//	@Data
//	@NoArgsConstructor
//	@AllArgsConstructor
//	public static class Field {
//		private String code;
//		private String type;
//		private String name;
//		private String description;
//		private Integer sizeXs;
//		private Integer sizeMd;
//		private Integer displayOrder;
//	}
//	
//}
