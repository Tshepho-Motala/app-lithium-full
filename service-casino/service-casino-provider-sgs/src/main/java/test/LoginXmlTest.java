//package test;
//
//import java.util.Date;
//
//import javax.xml.bind.JAXBContext;
//import javax.xml.bind.JAXBException;
//import javax.xml.bind.Marshaller;
//
//import org.junit.Test;
//
//import lithium.service.casino.provider.sgs.data.response.Extinfo;
//import lithium.service.casino.provider.sgs.data.response.LoginResult;
//import lithium.service.casino.provider.sgs.data.response.MethodResponse;
//import lithium.service.casino.provider.sgs.data.response.Response;
//
//public class LoginXmlTest {
//
//	
//	@Test
//	public void test() {
//		Extinfo extinfo = new Extinfo();
//		LoginResult lr = new LoginResult("seq_1234-56789-001100","token_1234567890", "sgs/loginname", "USD", "US", "New York", "50000", "0", "", "586948392845", "", "");
//		
//		lr.setErrorCode("12345");
//		lr.setErrorDescription("The error is described here");
//		MethodResponse<LoginResult, Extinfo> mr = new MethodResponse<>("login", new Date(), lr, extinfo);
//		Response<LoginResult, Extinfo> response = new Response<>(mr);
//		
//		JAXBContext jaxbContext;
//		try {
//			jaxbContext = JAXBContext.newInstance(Response.class, LoginResult.class);
//
//			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
//	
//			// output pretty printed
//			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
//	
//			jaxbMarshaller.marshal(response, System.out);
//		
//		} catch (JAXBException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
//
//}
