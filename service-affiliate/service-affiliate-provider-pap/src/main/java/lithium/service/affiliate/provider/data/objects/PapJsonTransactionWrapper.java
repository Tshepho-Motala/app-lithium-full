package lithium.service.affiliate.provider.data.objects;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class PapJsonTransactionWrapper {

	public PapJsonTransactionWrapper(String classType, String methodType, String sessionId, ArrayList<PapJsonTransaction> transactionList) {
		this.classType = classType;
		this.methodType = methodType;
		this.transactionList = transactionList;
		this.sessionId = sessionId;
	}
	
	@JsonProperty("C")
	private String classType;
	
	@JsonProperty("M")
	private String methodType;
	
	@JsonProperty("requests")
	private ArrayList<PapJsonTransaction> transactionList;
	
	@JsonProperty("S")
	private String sessionId;
	
	
//	public static void main(String[] args) {
	//	PapJsonTransaction pjt = new PapJsonTransaction("the_class_name", "the_method_Name");
	//	pjt.addField("magicname", "magicValue");
	//	pjt.addField("magicname_007", "magicValue_007");
	//	PapJsonTransaction pjt2 = new PapJsonTransaction("the_class_name_2", "the_method_Name_2");
	//	pjt2.addField("magicname_2", "magicValue_2");
	//	pjt2.addField("magicname_007_002", "magicValue_007_002");
	//	ArrayList<PapJsonTransaction> list = new ArrayList<>();
	//	
	//	list.add(pjt);
	//	list.add(pjt2);
	//	
	//	PapJsonTransactionWrapper pjtw = new PapJsonTransactionWrapper("wrapper_class", "wrapper_method", "1234424545ergfg", list);
	//	
	//	ObjectMapper mapper = new ObjectMapper();
	//	
	//	try {
	//		System.out.println(mapper.writeValueAsString(pjtw));
	//	} catch (JsonProcessingException e) {
	//		// TODO Auto-generated catch block
	//		e.printStackTrace();
	//	}
//	}
}
