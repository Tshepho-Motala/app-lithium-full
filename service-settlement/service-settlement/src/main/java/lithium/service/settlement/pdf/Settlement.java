package lithium.service.settlement.pdf;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import lithium.service.entity.client.objects.Entity;
import lithium.service.user.client.objects.User;

public class Settlement {
	private lithium.service.domain.client.objects.Domain domain = null;
	private byte[] domainLogo = null;
	private Header header = null;
	private Address physicalAddress = null;
	private Address billingAddress = null;
	private Detail detail = null;
	private List<SettlementEntry> rows = new ArrayList<>();
	
	private int maxRowSize = 23;
	private int maxPageWithSummation = 16;
	private int breakPoint = 12;
	
	private static final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
	
	public Settlement(
		lithium.service.domain.client.objects.Domain domain,
		byte[] domainLogo,
		lithium.service.settlement.data.entities.Settlement settlement,
		Entity entity,
		User user
	) {
		this.domain = domain;
		this.domainLogo = domainLogo;
		this.header = new Header(sdf.format(new Date()), settlement.getId().toString());
		this.detail = new Detail(entity != null? entity.getName(): user.getUsername(), sdf.format(settlement.getDateStart()), sdf.format(settlement.getDateEnd()));
		if (settlement.getPhysicalAddress() != null) {
			this.physicalAddress = new Address(settlement.getPhysicalAddress());
		}
		if (settlement.getBillingAddress() != null) {
			this.billingAddress = new Address(settlement.getBillingAddress());
		}
		settlement.getSettlementEntries().stream().forEach(entry -> {
			rows.add(new SettlementEntry(entry.getId(), sdf.format(entry.getDateStart()),
				sdf.format(entry.getDateEnd()), entry.getDescription(), entry.getAmount()));
		});
	}
	
	public byte[] printPDF() throws Exception {
		PDDocument pdfDocument = new PDDocument();
		
		PDPage pdfPage = new PDPage();
		pdfDocument.addPage(pdfPage);
		PDPageContentStream contents = new PDPageContentStream(pdfDocument, pdfPage);
		
		this.header.printPDF(this.domain, this.domainLogo, pdfDocument, contents);
		this.detail.printPDF(pdfDocument, contents);
		if (this.physicalAddress != null) {
			this.physicalAddress.printPDF(contents, false);
		}
		if (this.billingAddress != null) {
			this.billingAddress.printPDF(contents, true);
		}
		
		int rowY = 490;
		int numPrintedRows = 0;
		
		int rowsLeft = rows.size();
		
		printRowHeader(contents, rowY);
		printRowBackground(contents, rowY-21, 
			rowsLeft < this.maxPageWithSummation ? this.maxPageWithSummation : this.maxRowSize);
		
		BigDecimal total = BigDecimal.ZERO;
		for (SettlementEntry entry : rows) {
			numPrintedRows++;
			rowY -= 20;
			
			entry.printPDF(contents, rowY);
			
			total = total.add(entry.getAmount());
			
			if (newPageRequired(numPrintedRows, rowsLeft)) {
				rowsLeft -= numPrintedRows;
				numPrintedRows = 0;
				maxRowSize = 30;
				maxPageWithSummation = 23;
				breakPoint = 18;
				rowY = 640;
				contents = newPage(pdfDocument, contents, rowY,
					rowsLeft < this.maxPageWithSummation ? this.maxPageWithSummation : this.maxRowSize);
			}
		}
		
		printSummary(contents, total);
		printFooter(contents);
		contents.close();
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		pdfDocument.save(baos);
		pdfDocument.close();
		
		return baos.toByteArray();
	}
	
	private PDPageContentStream newPage(PDDocument pdfDocument, PDPageContentStream contents, int rowY, int numRows) throws Exception {
		contents.close();
		PDPage pdfPage = new PDPage();
		pdfDocument.addPage(pdfPage);
		contents = new PDPageContentStream(pdfDocument, pdfPage);
		this.header.printPDF(this.domain, this.domainLogo, pdfDocument, contents);
		printRowHeader(contents, rowY);
		printRowBackground(contents, rowY-21, numRows);
		return contents;
	}
	
	private boolean newPageRequired(int numPrintedRows, int rowsLeft) {
		if (numPrintedRows >= this.maxRowSize)
			return true;
		if (this.maxPageWithSummation < rowsLeft && rowsLeft < this.maxRowSize) {
			if (numPrintedRows >= this.breakPoint)
				return true;
		}
		return false;
	}
	
	private void printRowHeader(PDPageContentStream contents, int headerY) throws IOException {
		Color fillColor = new Color(230, 230, 230);
		Color strokeColor = new Color(100, 100, 100);
		contents.setStrokingColor(strokeColor);
		contents.setNonStrokingColor(fillColor);
		contents.addRect(50, headerY, 520, 20);
		contents.fillAndStroke();
		
		PDFont font = PDType1Font.HELVETICA;
		PDFPrinter headerPrinter = new PDFPrinter(contents, font, 12);
		headerPrinter.putText(60, headerY + 7, "Description");
		headerPrinter.putText(380, headerY + 7, "From");
		headerPrinter.putText(450, headerY + 7, "To");
		headerPrinter.putText(520, headerY + 7, "Amount");
	}
	
	private void printRowBackground(PDPageContentStream contents, int rowY, int numRows) throws IOException {
		Color strokeColor = new Color(100, 100, 100);
		contents.setStrokingColor(strokeColor);
		Color fillColor = new Color(240, 240, 240);
		contents.setNonStrokingColor(fillColor);
		
		boolean odd = true;
		for (int i = 0; i < numRows; i++) {
			if (odd) {
				contents.addRect(51, rowY, 518, 20);
				contents.fill();
			}
			
			contents.moveTo(50, rowY);
			contents.lineTo(50, rowY + 20);
			contents.moveTo(570, rowY);
			contents.lineTo(570, rowY + 20);
			contents.stroke();
			rowY -= 20;
			odd = !odd;
		}
		
		contents.moveTo(50, rowY + 20);
		contents.lineTo(570, rowY + 20);
		contents.stroke();
	}
	
	private void printSummary(PDPageContentStream contents, BigDecimal total) throws IOException {
		Color strokeColor = new Color(100, 100, 100);
		contents.setStrokingColor(strokeColor);
		
		PDFPrinter summaryLabelPrinter = new PDFPrinter(contents, PDType1Font.HELVETICA_BOLD, 8);
		PDFPrinter summaryValuePrinter = new PDFPrinter(contents, PDType1Font.HELVETICA, 12);
		
		total = total.setScale(2, RoundingMode.CEILING);
		
		int summaryStartY = 161;
		
		summaryLabelPrinter.putText(451, summaryStartY - 60, "Total");
		contents.addRect(450, summaryStartY - 60 - 17, 120, 16);
		contents.stroke();
		summaryValuePrinter.putTextToTheRight(566, summaryStartY - 60 - 13, this.domain.getCurrencySymbol() + " " + total.toString());
	}
	
	private void printFooter(PDPageContentStream contents) throws IOException {
		Color strokeColor = new Color(100, 100, 100);
		contents.setStrokingColor(strokeColor);
		contents.addRect(50, 25, 370, 135);
		contents.stroke();

		PDFPrinter footerLabelPrinter = new PDFPrinter(contents, PDType1Font.HELVETICA_BOLD, 8);
		footerLabelPrinter.putText(50, 162, "Notes");
	}
}
