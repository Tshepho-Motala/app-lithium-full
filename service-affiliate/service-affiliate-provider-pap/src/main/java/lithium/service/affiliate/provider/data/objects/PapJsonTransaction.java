package lithium.service.affiliate.provider.data.objects;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class PapJsonTransaction {

	public PapJsonTransaction(String classType, String methodType) {
		this.classType = classType;
		this.methodType = methodType;
		fieldList = new ArrayList<>();
		ArrayList<String> fieldHeader = new ArrayList<>();
		fieldHeader.add("name");
		fieldHeader.add("value");
	//	fieldHeader.add("values");
	//	fieldHeader.add("error");
		fieldList.add(fieldHeader); //template definition for actual fields to follow
	}
	
	@JsonProperty("C")
	private String classType;
	
	@JsonProperty("M")
	private String methodType;
	
	@JsonProperty("fields")
	private ArrayList<ArrayList<String>> fieldList;
	
	public void addField(final String name, final String value) {
		ArrayList<String> field = new ArrayList<>();
		field.add(name);
		field.add(value);
		//field.add(null);
		//field.add("");
		fieldList.add(field);
	}
	
//	public static void main(String[] args) {
//		PapJsonTransaction pjt = new PapJsonTransaction("the_class_name", "the_method_Name");
//		
//		pjt.addField("magicname", "magicValue");
//		pjt.addField("magicname_007", "magicValue_007");
//		
//		ObjectMapper mapper = new ObjectMapper();
//		
//		try {
//			System.out.println(mapper.writeValueAsString(pjt));
//		} catch (JsonProcessingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
}
