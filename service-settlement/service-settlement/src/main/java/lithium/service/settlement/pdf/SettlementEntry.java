package lithium.service.settlement.pdf;

import java.awt.Color;
import java.io.IOException;
import java.math.BigDecimal;

import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SettlementEntry {
	private Long id;
	private String dateStart;
	private String dateEnd;
	private String description;
	private BigDecimal amount;
	
	public void printPDF(PDPageContentStream contents, int rowY) throws IOException {
		Color strokeColor = new Color(100, 100, 100);
		contents.setStrokingColor(strokeColor);
		
		PDFont font = PDType1Font.HELVETICA;
		PDFPrinter textPrinter = new PDFPrinter(contents, font, 8);
		textPrinter.putText(60, rowY+7, this.description);
		textPrinter.putTextToTheRight(420, rowY+7, this.dateStart);
		textPrinter.putTextToTheRight(490, rowY+7, this.dateEnd);
		textPrinter.putTextToTheRight(560, rowY+7, this.amount.toString());
	}
}
