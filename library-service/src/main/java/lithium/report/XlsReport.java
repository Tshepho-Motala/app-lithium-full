package lithium.report;

import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;

public class XlsReport {
	
	protected String name;
	
	protected CellStyle styleHeading;
	protected CellStyle styleHeadingRight;
	protected CellStyle styleHeadingRotate;
	protected CellStyle styleNormal;
	protected CellStyle styleNormalCentered;
	protected CellStyle styleDate;
	protected CellStyle styleDateTime;
	protected CellStyle styleMoney;
	protected CellStyle styleMoneyWarn;
	
	protected SXSSFWorkbook wb;
	protected int rowNum = 0;
	protected int colNum = 0;
	protected Row row = null;
	protected Sheet sh = null;
	protected DataFormat moneyDataFormat ;

	private String defaultCurrencyCode = "";
	private String currencyTemplate = "_-[$%s]* #,##0.00_ ;_-[$%s]* -#,##0.00 ;_-[$%s]* \"-\"??_ ;_-@_ ";

	public XlsReport(String name) {
		this.name = name;
	}

	protected void createStyles() {
		
		Font font = wb.createFont();
		font.setBold(true);
		font.setColor(HSSFColor.BLUE.index);
		
		CreationHelper createHelper = wb.getCreationHelper();

		styleHeading = wb.createCellStyle();
		styleHeading.setBorderBottom(CellStyle.BORDER_THIN);
		styleHeading.setLeftBorderColor(HSSFColor.GREEN.index);
		styleHeading.setFillForegroundColor(HSSFColor.BRIGHT_GREEN.index);
		styleHeading.setFillBackgroundColor(HSSFColor.BRIGHT_GREEN.index);
		styleHeading.setFont(font);
		
		styleHeadingRight = wb.createCellStyle();
		styleHeadingRight.cloneStyleFrom(styleHeading);
		styleHeadingRight.setAlignment(CellStyle.ALIGN_RIGHT);
		
		styleHeadingRotate = wb.createCellStyle();
		styleHeadingRotate.cloneStyleFrom(styleHeading);
		styleHeadingRotate.setRotation((short) 90); 
		styleHeadingRotate.setAlignment(CellStyle.ALIGN_CENTER);
		
		styleNormal = wb.createCellStyle();

		styleNormalCentered = wb.createCellStyle();
		styleNormalCentered.setAlignment(CellStyle.ALIGN_CENTER);
		
		styleDate = wb.createCellStyle();
		styleDate.cloneStyleFrom(styleNormal);
		styleDate.setDataFormat(createHelper.createDataFormat().getFormat("dd/mm/yyyy"));

		styleDateTime = wb.createCellStyle();
		styleDateTime.cloneStyleFrom(styleNormal);
		styleDateTime.setDataFormat(createHelper.createDataFormat().getFormat("dd/mm/yyyy hh:mm"));

		styleMoney = wb.createCellStyle();
		styleMoney.cloneStyleFrom(styleNormal);
		moneyDataFormat = wb.createDataFormat();

		styleMoneyWarn = wb.createCellStyle();
		styleMoneyWarn.cloneStyleFrom(styleMoney);
		styleMoneyWarn.setFillBackgroundColor(HSSFColor.BLACK.index);
		styleMoneyWarn.setFillForegroundColor(HSSFColor.ROSE.index);
		styleMoneyWarn.setFillPattern(CellStyle.SOLID_FOREGROUND);
		Font font2 = wb.createFont();
		font2.setColor(HSSFColor.WHITE.index);
		styleMoneyWarn.setFont(font2);
	}
	
	protected void createCell(Row row, int position, CellStyle style, String value) {
		Cell c = row.createCell(position);
		c.setCellStyle(style);
		if (value == null) value = "";
		c.setCellValue(value);
	}

	protected void createNumericCell(Row row, int position, CellStyle style, Long value) {
		Cell c = row.createCell(position);
		c.setCellStyle(style);
		c.setCellType(Cell.CELL_TYPE_NUMERIC);
		if (value != null)
		c.setCellValue(value);
	}

	protected void createNumericCell(Row row, int position, CellStyle style, Double value) {
		Cell c = row.createCell(position);
		c.setCellStyle(style);
		c.setCellType(Cell.CELL_TYPE_NUMERIC);
		if (value != null)
		c.setCellValue(value);
	}

	protected void createNumericCell(Row row, int position, CellStyle style, Integer value) {
		Cell c = row.createCell(position);
		c.setCellStyle(style);
		c.setCellType(Cell.CELL_TYPE_NUMERIC);
		if (value != null)
		c.setCellValue(value);
	}

	protected void createMoneyCellCents(Row row, int position, CellStyle style, Long value, String currencyCode) {
		String currencyFormat = String.format(currencyTemplate, currencyCode,currencyCode,currencyCode);
		style.setDataFormat(moneyDataFormat.getFormat(currencyFormat));
		createMoneyCellCents(row, position, style, value);
	}

	protected void createMoneyCellCents(Row row, int position, CellStyle style, Long value) {
		Cell c = row.createCell(position);
		c.setCellStyle(style);
		c.setCellType(Cell.CELL_TYPE_NUMERIC);
		if (value != null)
			c.setCellValue(((double) value.longValue()) / 100.0);
	}

	protected void createDateCell(Row row, int position, CellStyle style, Date value) {
		Cell c = row.createCell(position);
		c.setCellStyle(style);
		if (value != null)
		c.setCellValue(value);
	}
	
	public void cell(String value) {
		createCell(row, colNum++, styleNormal, value);
	}

	public void cellCents(Long value) {
		cellCents(value, false, defaultCurrencyCode);
	}

	public void cellCents(Long value, String currencyCode) {
		cellCents(value, false, currencyCode);
	}

	public void cellCents(Long value, boolean warn, String currencyCode) {
		createMoneyCellCents(row, colNum++, warn ? styleMoneyWarn : styleMoney, value, currencyCode);
	}

	public void cellDate(Date value) {
		createDateCell(row, colNum++, styleDate, value);
	}
	
	public void cellDateTime(Date value) {
		createDateCell(row, colNum++, styleDateTime, value);
	}
	
	public void cellNumeric(Double value) {
		createNumericCell(row, colNum++, styleNormal, value);
	}

	public void cellNumeric(Long value) {
		createNumericCell(row, colNum++, styleNormal, value);
	}

	public void cellNumeric(Integer value) {
		createNumericCell(row, colNum++, styleNormal, value);
	}

//	protected void send(HttpServletResponse response) throws IOException {
//		String fileName = name + ".xlsx";
//		String mimeType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
//		response.setContentType(mimeType);
//		response.setHeader("Content-Disposition", String.format("attachment; filename=\"" + fileName +"\""));
//		wb.write(response.getOutputStream());
//	}
	
	public void run(OutputStream outputStream, WorkbookCallback callback) throws IOException {
		
		wb = new SXSSFWorkbook();
		createStyles();
		
		try {
			wb.setCompressTempFiles(true);
			callback.workbook();
			wb.write(outputStream);
			wb.close();
		} finally {
			wb.dispose();
		}
		
		outputStream.flush();

	}
	
	public void sheet(String sheetName, HeaderCallback headerCallback, ContentCallback contentCallback) {
		sh = wb.createSheet(sheetName);
		rowNum = 0;
		colNum = 0;
		row = sh.createRow(rowNum++);
		headerCallback.header();
		contentCallback.content();
//		for (int i = 0; i < colNum; i++) sh.autoSizeColumn(i);
		sh.createFreezePane(1, 1);
	}
	
	public void sheet(String sheetName, boolean autoSizeColumns, HeaderCallback headerCallback, ContentCallback contentCallback) {
		sh = wb.createSheet(sheetName);
		rowNum = 0;
		colNum = 0;
		row = sh.createRow(rowNum++);
		headerCallback.header();
		contentCallback.content();
		if (autoSizeColumns) for (int i = 0; i < colNum; i++) sh.autoSizeColumn(i);
		sh.createFreezePane(1, 1);
	}

	public void sheet(String sheetName, List<Integer> autoColumnIds, HeaderCallback headerCallback, ContentCallback contentCallback ) {
		sh = wb.createSheet(sheetName);
		rowNum = 0;
		colNum = 0;
		row = sh.createRow(rowNum++);
		headerCallback.header();
		contentCallback.content();
		autoColumnIds.stream().forEach(colNumber -> sh.autoSizeColumn(colNumber));
		sh.createFreezePane(1, 1);
	}
	
	public void row(RowCallback rowCallback) {
		row = sh.createRow(rowNum++);
		colNum = 0;
		rowCallback.row();
	}

	public interface WorkbookCallback {
		public void workbook();
	}

	public interface ContentCallback {
		public void content();
	}

	public interface HeaderCallback {
		public void header();
	}
	
	public interface RowCallback {
		public void row();
	}
	
	public void columnHeading(String title) {
//		sh.setColumnWidth(colNum, 500);
		createCell(row, colNum++, styleHeading, title);
	}

	public void setDefaultCurrencyCode(String code) {
		this.defaultCurrencyCode = code;
	}
}
