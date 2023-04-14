package lithium.service.settlement.pdf;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;

import javax.imageio.ImageIO;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import lithium.service.domain.client.objects.Domain;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Header {
	private String settlementDate;
	private String settlementNumber;
	
	public void printPDF(Domain domain, byte[] domainLogo, PDDocument pdfDocument, PDPageContentStream contents) throws Exception {
		if (domainLogo != null) {
			ByteArrayInputStream bais = new ByteArrayInputStream(domainLogo);
			BufferedImage bim = ImageIO.read(bais);
			PDImageXObject pdImage = LosslessFactory.createFromImage(pdfDocument, bim);
			final float width = 60f;
			final float scale = width / pdImage.getWidth();
			contents.drawImage(pdImage, 50, 720, width, pdImage.getHeight()*scale);
		}
		
		PDFont headerFont = PDType1Font.HELVETICA_BOLD;
		PDFPrinter headerPrinter = new PDFPrinter(contents, headerFont, 16);
		headerPrinter.putText(120, 740, domain.getDisplayName());
		
		lithium.service.domain.client.objects.Address postalAddress = domain.getPostalAddress();
		lithium.service.domain.client.objects.Address physicalAddress = domain.getPhysicalAddress();
		
		PDFont font = PDType1Font.HELVETICA;
		PDFPrinter textPrinter = new PDFPrinter(contents, font, 10);
		
		if (postalAddress != null || physicalAddress != null) {
			boolean postal = postalAddress != null;
			
			int x = 120;
			int y = 720;
			textPrinter.putText(x, y, postal? postalAddress.getAddressLine1(): physicalAddress.getAddressLine1());
			y -= 12;
			if (postal? postalAddress.getAddressLine2() != null: physicalAddress.getAddressLine2() != null) {
				textPrinter.putText(x, y, postal? postalAddress.getAddressLine2(): physicalAddress.getAddressLine2());
				y -= 12;
			}
			if (postal? postalAddress.getAddressLine3() != null: physicalAddress.getAddressLine3() != null) {
				textPrinter.putText(x, y, postal? postalAddress.getAddressLine3(): physicalAddress.getAddressLine3());
			}
			textPrinter.putText(x, y, postal? postalAddress.getCity(): physicalAddress.getCity());
			y -= 12;
			textPrinter.putText(x, y, postal? postalAddress.getAdminLevel1(): physicalAddress.getAdminLevel1());
			y -= 12;
			textPrinter.putText(x, y, postal? postalAddress.getCountry(): physicalAddress.getCountry());
			y -= 12;
			textPrinter.putText(x, y, postal? postalAddress.getPostalCode(): physicalAddress.getPostalCode());
			y -= 12;
		}
		
		Color color = new Color(200, 200, 200);
		PDFPrinter invoiceHeaderPrinter = new PDFPrinter(contents, font, 24, color);
		invoiceHeaderPrinter.putText(410, 740, "SETTLEMENT");
		
		textPrinter.putText(410, 710, "Settlement date:");
		textPrinter.putText(410, 698, "Settlement number:");
		textPrinter.putText(510, 710, getSettlementDate());
		textPrinter.putText(510, 698, getSettlementNumber());
	}
}
