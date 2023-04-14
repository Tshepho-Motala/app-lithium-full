package lithium.service.settlement.pdf;

import java.awt.Color;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Detail {
	private String entityName;
	private String dateStart;
	private String dateEnd;
	
	public void printPDF(PDDocument pdfDocument, PDPageContentStream contents) throws IOException {
		Color strokeColor = new Color(100, 100, 100);
		Color fillColor = new Color(240, 240, 240);
		contents.setStrokingColor(strokeColor);
		contents.setNonStrokingColor(fillColor);
		contents.addRect(50, 520, 520, 20);
		contents.stroke();
		
		PDFont font = PDType1Font.HELVETICA_OBLIQUE;
		PDFPrinter detailsPrinter = new PDFPrinter(contents, font, 10);
		detailsPrinter.putText(60, 527, "SETTLEMENT STATEMENT FOR (" + entityName.toUpperCase() + ") FOR THE PERIOD (" + dateStart + " TO " + dateEnd + ")");
	}
}
