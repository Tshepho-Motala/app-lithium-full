package lithium.service.settlement.pdf;

import java.awt.Color;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

public class Address {
	private String addressLine1;
	private String addressLine2;
	private String addressLine3;
	private String city;
	private String adminLevel1;
	private String country;
	private String postalCode;
	
	public Address(lithium.service.settlement.data.entities.Address address) {
		this.addressLine1 = address.getAddressLine1();
		this.addressLine2 = address.getAddressLine2();
		this.addressLine3 = address.getAddressLine3();
		this.city = address.getCity();
		this.adminLevel1 = address.getAdminLevel1();
		this.country = address.getCountry();
		this.postalCode = address.getPostalCode();
	}
	
	public void printPDF(PDPageContentStream contents, boolean rightSide) throws IOException {
		PDFont headerFont = PDType1Font.HELVETICA_BOLD;
		PDFont font = PDType1Font.HELVETICA;
		Color color = new Color(80, 80, 80);
		
		int x = rightSide ? 410 : 120;
		
		int y = 640;
		
		PDFPrinter headerPrinter = new PDFPrinter(contents, headerFont, 10);
		headerPrinter.putText(x, y, rightSide ? "Billing address:" : "Physical address:");
		y -= 12;
		
		PDFPrinter addressPrinter = new PDFPrinter(contents, font, 10, color);
		addressPrinter.putText(x, y, this.addressLine1);
		y -= 12;
		if (this.addressLine2 != null) {
			addressPrinter.putText(x, y, this.addressLine2);
			y -= 12;
		}
		if (this.addressLine3 != null) {
			addressPrinter.putText(x, y, this.addressLine3);
			y -= 12;
		}
		if (this.city != null) {
			addressPrinter.putText(x, y, this.city);
			y -= 12;
		}
		if (this.adminLevel1 != null) {
			addressPrinter.putText(x, y, this.adminLevel1);
			y -= 12;
		}
		if (this.country != null) {
			addressPrinter.putText(x, y, this.country);
			y -= 12;
		}
		if (this.postalCode != null) {
			addressPrinter.putText(x, y, this.postalCode);
			y -= 12;
		}
	}
}
